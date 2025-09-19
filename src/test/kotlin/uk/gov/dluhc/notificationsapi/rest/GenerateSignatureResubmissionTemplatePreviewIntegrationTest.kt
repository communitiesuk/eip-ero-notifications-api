package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildSignatureResubmissionPersonalisation
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.MILLIS

internal class GenerateSignatureResubmissionTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "4bd12b8b-f2a8-4d5a-8fb6-69ed5f9e90ae"
        private const val POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "40b4ae6e-0c8c-4267-a843-24566dd8711e"
        private const val POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "6bd16119-4143-4fc4-8da6-6834a6b3400e"
        private const val POSTAL_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "ba3e64b9-40a5-46a2-b867-c7e2f9ffad5d"
        private const val POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "b137eb58-19ae-409f-8c27-b5c3fe17c265"
        private const val POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "1af61438-5bca-4adb-b1a6-9dea7ecc575f"
        private const val POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "970671d3-06ab-4bf1-979c-8aca60b7368a"
        private const val POSTAL_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "deef9aa2-7790-447f-8af1-f921ce21958c"
        private const val PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "4bd12b8b-f2a8-4d5a-8fb6-69ed5f9e90ae"
        private const val PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "40b4ae6e-0c8c-4267-a843-24566dd8711e"
        private const val PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "6bd16119-4143-4fc4-8da6-6834a6b3400e"
        private const val PROXY_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "ba3e64b9-40a5-46a2-b867-c7e2f9ffad5d"
        private const val PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "b137eb58-19ae-409f-8c27-b5c3fe17c265"
        private const val PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "1af61438-5bca-4adb-b1a6-9dea7ecc575f"
        private const val PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "970671d3-06ab-4bf1-979c-8aca60b7368a"
        private const val PROXY_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "deef9aa2-7790-447f-8af1-f921ce21958c"
        private const val URI_TEMPLATE = "/templates/signature-resubmission/preview"
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
            .contentType(APPLICATION_JSON)
            .exchange()
            .expectStatus().isUnauthorized
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withAValidBody() // Default request body has reasons
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
            .hasMessageContaining("Notification template not found for the given template type")
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
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withAValidBody() // Default request body has reasons
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

        val requestBody = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            personalisation = buildSignatureResubmissionPersonalisation(
                applicationReference = "",
                firstName = "",
                eroContactDetails = buildEroContactDetails(
                    address = buildAddress(
                        street = "",
                        postcode = "AB11111111111",
                    ),
                ),
            ),
            sourceType = SourceType.POSTAL,
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
            .hasMessageContaining("Validation failed for object='generateSignatureResubmissionTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [AB11111111111], size must be between 1 and 10")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false,false,post,postal vote",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false,false,post,postal vote",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,false,false,drwy'r post,bleidlais bost",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,false,false,drwy'r post,bleidlais bost",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false,false,proxy,proxy vote",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false,false,proxy,proxy vote",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,false,false,drwy ddirprwy,bleidlais drwy ddirprwy",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,false,false,drwy ddirprwy,bleidlais drwy ddirprwy",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,false,true,post,postal vote",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,false,true,post,postal vote",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,false,true,drwy'r post,bleidlais bost",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,false,true,drwy'r post,bleidlais bost",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,false,true,proxy,proxy vote",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,false,true,proxy,proxy vote",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,false,true,drwy ddirprwy,bleidlais drwy ddirprwy",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,false,true,drwy ddirprwy,bleidlais drwy ddirprwy",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,true,false,post,postal vote",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,true,false,post,postal vote",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,true,false,drwy'r post,bleidlais bost",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,true,false,drwy'r post,bleidlais bost",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,true,false,proxy,proxy vote",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,true,false,proxy,proxy vote",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,true,false,drwy ddirprwy,bleidlais drwy ddirprwy",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,true,false,drwy ddirprwy,bleidlais drwy ddirprwy",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true,true,post,postal vote",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true,true,post,postal vote",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true,true,drwy'r post,bleidlais bost",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true,true,drwy'r post,bleidlais bost",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true,true,proxy,proxy vote",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true,true,proxy,proxy vote",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true,true,drwy ddirprwy,bleidlais drwy ddirprwy",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true,true,drwy ddirprwy,bleidlais drwy ddirprwy",
        ],
    )
    fun `should return template preview given valid request`(
        sourceType: SourceType,
        communicationChannel: CommunicationChannel,
        templateId: String,
        language: Language,
        isRejected: Boolean,
        withReasons: Boolean,
        expectedShortSourceType: String,
        expectedFullSourceType: String,
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            sourceType = sourceType,
            channel = communicationChannel,
            language = language,
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionReasons = if (withReasons) listOf(SignatureRejectionReason.PARTIALLY_MINUS_CUT_MINUS_OFF) else emptyList(),
                rejectionNotes = if (withReasons) "Invalid" else null,
                rejectionFreeText = "Free Text",
                isRejected = isRejected,
            ),
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
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse)
        val expectedRejectionReasons = if (language == Language.EN) listOf("The image has some of it cut off") else listOf("Mae darn o'r llun wedi'i dorri i ffwrdd")
        val shouldShowText = isRejected && !withReasons
        val sourceTypeText = getExpectedSourceTypeText(language, sourceType)
        val expectedNotSuitableText = if (language == Language.EN) "The signature you provided in your $sourceTypeText application is not suitable." else "Nid yw'r llofnod y gwnaethoch ei ddarparu yn eich cais am $sourceTypeText yn addas."
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mutableMapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectionReasons" to if (withReasons) expectedRejectionReasons else emptyList(),
                "rejectionNotes" to (requestBody.personalisation.rejectionNotes ?: ""),
                "rejectionFreeText" to (rejectionFreeText ?: ""),
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to eroContactDetails.address.property!!,
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to eroContactDetails.address.town!!,
                "eroAddressLine4" to eroContactDetails.address.area!!,
                "eroAddressLine5" to eroContactDetails.address.locality!!,
                "eroPostcode" to eroContactDetails.address.postcode,
                "fullSourceType" to expectedFullSourceType,
                "shortSourceType" to expectedShortSourceType,
                "uploadSignatureLink" to uploadSignatureLink,
                "signatureNotSuitableText" to if (shouldShowText) expectedNotSuitableText else "",
                "deadline" to "",
            )
        }
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    private fun getExpectedSourceTypeText(language: Language, sourceType: SourceType): String {
        return if (language == Language.EN) {
            if (sourceType == SourceType.POSTAL) {
                "postal vote"
            } else {
                "proxy vote"
            }
        } else {
            if (sourceType == SourceType.POSTAL) {
                "bleidlais bost"
            } else {
                "bleidlais drwy ddirprwy"
            }
        }
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        withABody(buildGenerateSignatureResubmissionTemplatePreviewRequest(sourceType = SourceType.PROXY))

    private fun WebTestClient.RequestBodySpec.withABody(request: GenerateSignatureResubmissionTemplatePreviewRequest): WebTestClient.RequestBodySpec {
        return body(
            Mono.just(request),
            GenerateRejectedSignatureTemplatePreviewRequest::class.java,
        ) as WebTestClient.RequestBodySpec
    }
}
