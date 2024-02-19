package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateParentGuardianRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildParentGuardianRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildParentGuardianTemplatePreviewRequest
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateParentGuardianRequiredTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val PARENT_GUARDIAN_REQUIRED_EMAIL_EN_TEMPLATE_ID = "206e2ea4-c2d4-412c-8abb-59bc9d57445b"
        private const val PARENT_GUARDIAN_REQUIRED_EMAIL_CY_TEMPLATE_ID = "450f8f7a-5821-4b71-a6ab-372e48b086e2"
        private const val PARENT_GUARDIAN_REQUIRED_LETTER_EN_TEMPLATE_ID = "273febb3-fe97-4ae5-a4d6-dfd57cc8c6d8"
        private const val PARENT_GUARDIAN_REQUIRED_LETTER_CY_TEMPLATE_ID = "20f8f805-fac0-453c-871e-41f1d9e0eb29"
        private const val URI_TEMPLATE = "/templates/parent-guardian-required/preview"
    }

    @BeforeEach
    fun setup() {
        wireMockService.stubCognitoJwtIssuerResponse()
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return forbidden given no bearer token`() {
        webTestClient.post()
            .uri(URI_TEMPLATE)
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return not found given non existing template`() {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(
            PARENT_GUARDIAN_REQUIRED_EMAIL_EN_TEMPLATE_ID
        )
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withAValidBody()
            .exchange()
            .expectStatus()
            .isNotFound
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(404)
            .hasError("Not Found")
            .hasMessage("Notification template not found for the given template type")
            .hasNoValidationErrors()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "$PARENT_GUARDIAN_REQUIRED_EMAIL_EN_TEMPLATE_ID, EMAIL, EN",
            "$PARENT_GUARDIAN_REQUIRED_LETTER_EN_TEMPLATE_ID, LETTER, EN",
            "$PARENT_GUARDIAN_REQUIRED_EMAIL_CY_TEMPLATE_ID, EMAIL, CY",
            "$PARENT_GUARDIAN_REQUIRED_LETTER_CY_TEMPLATE_ID, LETTER, CY"
        ]
    )
    fun `should return template preview given valid json request`(
        templateId: String,
        channel: NotificationChannel,
        language: Language
    ) {
        // Given
        val notifyClientResponse =
            NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildParentGuardianTemplatePreviewRequest(
            channel = channel,
            language = language,
            personalisation = buildParentGuardianRequiredPersonalisation(
                applicationReference = "applicationReference",
                freeText = "free text",
                eroContactDetails = buildEroContactDetails(
                    localAuthorityName = "Barcelona",
                    address = buildAddress(
                        street = "some street",
                        postcode = "postcode"
                    ),
                )
            )
        )

        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf<String, Any>(
                "applicationReference" to applicationReference,
                "LAName" to eroContactDetails.localAuthorityName,
                "firstName" to firstName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to eroContactDetails.address.property!!,
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to eroContactDetails.address.town!!,
                "eroAddressLine4" to eroContactDetails.address.area!!,
                "eroAddressLine5" to eroContactDetails.address.locality!!,
                "eroPostcode" to eroContactDetails.address.postcode,
                "freeText" to freeText!!
            )
        }
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(
            templateId,
            expectedPersonalisationDataMap
        )
    }
}

private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
    body(
        Mono.just(buildParentGuardianTemplatePreviewRequest(sourceType = SourceType.OVERSEAS)),
        GenerateParentGuardianRequiredTemplatePreviewRequest::class.java
    ) as WebTestClient.RequestBodySpec
