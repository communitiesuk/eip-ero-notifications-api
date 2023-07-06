package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.MILLIS

internal class GenerateRejectedSignatureTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "ea7dac2e-81b8-47ea-90a3-2d3fbc1c2394"
        private const val POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "747fa9dc-f59a-4669-bf32-d3b59b20852b"
        private const val POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "34250705-346a-4e24-9110-0a46f4e65dda"
        private const val POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "08687c2c-225e-4e99-a1f1-ee884ddbbba3"
        private const val PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "7fa64777-222f-45e9-937b-6236359b79df"
        private const val PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "a934fb1a-a199-41cc-829d-bf025ad1b740"
        private const val PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "29ba37c4-f857-4f56-934a-5ff86b81204f"
        private const val PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "36c4aeed-abae-4a36-ab83-97000381a62a"
        private const val URI_TEMPLATE = "/templates/rejected-signature/preview"
    }

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @BeforeEach
    fun setup() {
        wireMockService.stubCognitoJwtIssuerResponse()
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(APPLICATION_JSON)
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)

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

    @Test
    fun `should return bad request given missing request body`() {
        // Given
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining("Required request body is missing")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return bad request given valid missing request body parameters to gov notify`() {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withAValidBody()
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining("Missing personalisation: applicationReference, firstName")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return bad request given invalid request with invalid fields`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()

        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            personalisation = buildRejectedSignaturePersonalisation(
                applicationReference = "",
                firstName = "",
                eroContactDetails = buildEroContactDetails(
                    address = buildAddress(
                        street = "",
                        postcode = "AB11111111111"
                    )
                )
            )
        )
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)
        val expectedValidationErrorsCount = 4

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining("Validation failed for object='generateRejectedSignatureTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [AB11111111111], size must be between 1 and 10")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY"
        ]
    )
    fun `should return template preview given valid request`(
        sourceType: SourceType,
        notificationChannel: NotificationChannel,
        templateId: String,
        language: Language,
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            sourceType = sourceType,
            channel = notificationChannel,
            language = language,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = listOf("Invalid signature"),
                rejectionNotes = "Invalid",
                rejectionFreeText = "Free Text"
            )
        )

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actualResponse = response.responseBody.blockFirst()
        val expectedResponse = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }
        assertThat(actualResponse).isEqualTo(expectedResponse)
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mutableMapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectionReasons" to requestBody.personalisation.rejectionReasons,
                "rejectionNotes" to rejectionNotes!!,
                "rejectionFreeText" to rejectionFreeText!!,
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to eroContactDetails.address.property!!,
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to eroContactDetails.address.town!!,
                "eroAddressLine4" to eroContactDetails.address.area!!,
                "eroAddressLine5" to eroContactDetails.address.locality!!,
                "eroPostcode" to eroContactDetails.address.postcode
            )
        }
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL,EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID",
            "POSTAL,LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID",
            "PROXY,EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID",
            "PROXY,LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID"
        ]
    )
    fun `should return template preview given valid request when optional values are not populated`(
        sourceType: SourceType,
        notificationChannel: NotificationChannel,
        templateId: String
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            channel = notificationChannel,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = emptyList(),
                rejectionNotes = null,
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull())
            )
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectionReasons" to emptyArray<String>(),
                "rejectionNotes" to "",
                "rejectionFreeText" to "",
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to "",
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to "",
                "eroAddressLine4" to "",
                "eroAddressLine5" to "",
                "eroPostcode" to eroContactDetails.address.postcode
            )
        }

        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL,EN,VOTER_MINUS_CARD",
            "EMAIL,CY,VOTER_MINUS_CARD",
            "LETTER,EN,VOTER_MINUS_CARD",
            "LETTER,CY,VOTER_MINUS_CARD",
        ]
    )
    fun `should return bad request if a template is not configured`(
        notificationChannel: NotificationChannel,
        language: Language?,
        sourceType: SourceType
    ) {

        // Given
        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            channel = notificationChannel,
            language = language,
            sourceType = sourceType,
        )
        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        val sourceTypeValue = sourceTypeMapper.fromApiToDto(sourceType)
        val expectedErrorMessage =
            "No ${notificationChannel.name.lowercase()} template defined in ${language.toMessage()} for notification type ${NotificationType.REJECTED_SIGNATURE} and sourceType $sourceTypeValue"
        assertThat(actual)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining(expectedErrorMessage)
            .hasNoValidationErrors()
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        withABody(buildGenerateRejectedSignatureTemplatePreviewRequest(sourceType = SourceType.PROXY))

    private fun WebTestClient.RequestBodySpec.withABody(request: GenerateRejectedSignatureTemplatePreviewRequest): WebTestClient.RequestBodySpec {
        return body(
            Mono.just(request),
            GenerateRejectedSignatureTemplatePreviewRequest::class.java
        ) as WebTestClient.RequestBodySpec
    }
}

private fun Language?.toMessage(): String = if (this == Language.CY) "Welsh" else "English"
