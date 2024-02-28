package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateRequiredOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.OverseasDocumentType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRequiredOverseasDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRequiredOverseasDocumentTemplatePreviewRequest
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateRequiredOverseasDocumentTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val PARENT_GUARDIAN_PROOF_REQUIRED_EMAIL_EN_TEMPLATE_ID = "206e2ea4-c2d4-412c-8abb-59bc9d57445b"
        private const val PARENT_GUARDIAN_PROOF_REQUIRED_EMAIL_CY_TEMPLATE_ID = "450f8f7a-5821-4b71-a6ab-372e48b086e2"
        private const val PARENT_GUARDIAN_PROOF_REQUIRED_LETTER_EN_TEMPLATE_ID = "273febb3-fe97-4ae5-a4d6-dfd57cc8c6d8"
        private const val PARENT_GUARDIAN_PROOF_REQUIRED_LETTER_CY_TEMPLATE_ID = "20f8f805-fac0-453c-871e-41f1d9e0eb29"
        private const val NINO_NOT_MATCHED_EMAIL_EN_TEMPLATE_ID = "cea6ecec-7627-4322-b220-ee70a69f014d"
        private const val NINO_NOT_MATCHED_EMAIL_CY_TEMPLATE_ID = "1f922de4-1e70-4aaf-9b76-7ab789fe7d1a"
        private const val NINO_NOT_MATCHED_LETTER_EN_TEMPLATE_ID = "0305c716-cfbc-401d-b95b-a6aeda741ba6"
        private const val NINO_NOT_MATCHED_LETTER_CY_TEMPLATE_ID = "abd343c5-edab-4e58-82b4-293736a464d0"
        private const val QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_EMAIL_EN_TEMPLATE_ID =
            "00dd5dc0-9573-41ae-a3ac-2bd678f1c84a"
        private const val QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_EMAIL_CY_TEMPLATE_ID =
            "9b6d00f8-d0f9-4921-9523-f69681f2b70b"
        private const val QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_LETTER_EN_TEMPLATE_ID =
            "8110954f-72d3-49ce-bbd1-fdfc22e7bde7"
        private const val QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_LETTER_CY_TEMPLATE_ID =
            "9a207ce7-150c-425d-beac-89c39c2bd689"
        private const val URI_TEMPLATE = "/templates/required-overseas-document/preview"
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

    @ParameterizedTest
    @CsvSource(
        "$PARENT_GUARDIAN_PROOF_REQUIRED_EMAIL_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN",
        "$QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_EMAIL_EN_TEMPLATE_ID, QUALIFYING_MINUS_ADDRESS",
        "$NINO_NOT_MATCHED_EMAIL_EN_TEMPLATE_ID, IDENTITY"
    )
    fun `should return not found given non existing template`(
        templateId: String,
        overseasDocumentType: OverseasDocumentType
    ) {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(
            templateId
        )

        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withAValidBody(overseasDocumentType)
            .exchange()
            .expectStatus()
            .isNotFound
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.Companion.assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(404)
            .hasError("Not Found")
            .hasMessage("Notification template not found for the given template type")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return error is there is no country in address template`() {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(
            NINO_NOT_MATCHED_EMAIL_CY_TEMPLATE_ID
        )

        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        val requestBody = buildRequiredOverseasDocumentTemplatePreviewRequest(
            channel = NotificationChannel.EMAIL,
            language = Language.EN,
            overseasDocumentType = OverseasDocumentType.IDENTITY,
            personalisation = buildRequiredOverseasDocumentPersonalisation(
                applicationReference = "applicationReference",
                eroContactDetails = buildEroContactDetails(
                    localAuthorityName = "Barcelona",
                    address = buildAddress(
                        street = "some street",
                        postcode = "postcode",
                        country = null
                    ),
                ),
            )
        )

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GenerateRequiredOverseasDocumentTemplatePreviewRequest::class.java
            )
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.Companion.assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasMessage("Country is required to process a template for overseas")
            .hasNoValidationErrors()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "$PARENT_GUARDIAN_PROOF_REQUIRED_EMAIL_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, EMAIL, EN",
            "$PARENT_GUARDIAN_PROOF_REQUIRED_EMAIL_CY_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, EMAIL, CY",
            "$PARENT_GUARDIAN_PROOF_REQUIRED_LETTER_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, LETTER, EN",
            "$PARENT_GUARDIAN_PROOF_REQUIRED_LETTER_CY_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, LETTER, CY",
            "$NINO_NOT_MATCHED_EMAIL_EN_TEMPLATE_ID, IDENTITY, EMAIL, EN",
            "$NINO_NOT_MATCHED_EMAIL_CY_TEMPLATE_ID, IDENTITY, EMAIL, CY",
            "$NINO_NOT_MATCHED_LETTER_EN_TEMPLATE_ID, IDENTITY, LETTER, EN",
            "$NINO_NOT_MATCHED_LETTER_CY_TEMPLATE_ID, IDENTITY, LETTER, CY",
            "$QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_EMAIL_EN_TEMPLATE_ID, QUALIFYING_MINUS_ADDRESS, EMAIL, EN",
            "$QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_EMAIL_CY_TEMPLATE_ID, QUALIFYING_MINUS_ADDRESS, EMAIL, CY",
            "$QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_LETTER_EN_TEMPLATE_ID, QUALIFYING_MINUS_ADDRESS, LETTER, EN",
            "$QUALIFYING_ADDRESS_DOCUMENT_REQUIRED_LETTER_CY_TEMPLATE_ID, QUALIFYING_MINUS_ADDRESS, LETTER, CY",
        ]
    )
    fun `should return template preview given valid json request`(
        templateId: String,
        overseasDocumentType: OverseasDocumentType,
        channel: NotificationChannel,
        language: Language
    ) {
        // Given
        val notifyClientResponse =
            NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildRequiredOverseasDocumentTemplatePreviewRequest(
            channel = channel,
            language = language,
            overseasDocumentType = overseasDocumentType,
            personalisation = buildRequiredOverseasDocumentPersonalisation(
                applicationReference = "applicationReference",
                eroContactDetails = buildEroContactDetails(
                    localAuthorityName = "Barcelona",
                    address = buildAddress(
                        street = "some street",
                        postcode = "postcode",
                        country = "Spain"
                    ),
                ),
            )
        )

        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf<String, Any>(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "requiredDocumentFreeText" to "",
                "LAName" to eroContactDetails.localAuthorityName,
                "eroPhone" to eroContactDetails.phone,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroAddressLine1" to eroContactDetails.address.property!!,
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to eroContactDetails.address.town!!,
                "eroAddressLine4" to eroContactDetails.address.area!!,
                "eroAddressLine5" to eroContactDetails.address.locality!!,
                "eroPostcode" to eroContactDetails.address.postcode,
                "eroCountry" to eroContactDetails.address.country!!
            )
        }
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GenerateRequiredOverseasDocumentTemplatePreviewRequest::class.java
            )
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

private fun WebTestClient.RequestBodySpec.withAValidBody(overseasDocumentType: OverseasDocumentType): WebTestClient.RequestBodySpec =
    body(
        Mono.just(buildRequiredOverseasDocumentTemplatePreviewRequest(overseasDocumentType = overseasDocumentType)),
        GenerateRejectedOverseasDocumentTemplatePreviewRequest::class.java
    ) as WebTestClient.RequestBodySpec
