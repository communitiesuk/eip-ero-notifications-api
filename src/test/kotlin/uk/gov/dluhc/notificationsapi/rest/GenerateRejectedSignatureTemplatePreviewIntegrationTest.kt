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
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
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
        private const val POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "8fb82c1f-1794-4394-b56e-57c768613293"
        private const val POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "c3e6854d-8ad3-4ee7-b112-7b52529732e4"
        private const val POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "a427c8f4-4b6d-4ce5-9378-57b506da6a77"
        private const val POSTAL_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "ad1019c8-a904-4d5f-b77a-bc9e25bbe32e"
        private const val POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "8f36972f-2a19-4b73-8186-650fd12d58e7"
        private const val POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "5da3bf04-e35a-47ea-a862-a00b3cf004f2"
        private const val POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "885739b7-b515-4f9f-89bb-cf5dd18b4fcc"
        private const val POSTAL_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "4f95fe01-0459-4220-9c62-241be81e76c1"
        private const val PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID = "080066e2-d598-40d8-b3e1-ca499bdc9d0e"
        private const val PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID = "43c0b489-4931-47f0-9cac-aed49839b0d2"
        private const val PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "aae7430b-99ad-49e3-99fc-041e4361a8ba"
        private const val PROXY_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "d922e924-2972-4746-aaf4-6c82405e7840"
        private const val PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID = "ba0bbe1c-b8a6-420f-a6b6-3ddcd257dabb"
        private const val PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID = "c405a231-f46a-4264-8bdb-ea4542cae378"
        private const val PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID = "ea0ef1fc-6ba0-4aea-8e0a-030e43b0d41e"
        private const val PROXY_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID = "5de224e5-0fdb-4f13-b090-afc04d05c121"
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
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,false",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,false",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_ENGLISH_TEMPLATE_ID,EN,false",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WELSH_TEMPLATE_ID,CY,false",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WELSH_TEMPLATE_ID,CY,false",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true",
            "POSTAL, EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true",
            "POSTAL, LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,EN,true",
            "PROXY, EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true",
            "PROXY, LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_WELSH_TEMPLATE_ID,CY,true"
        ]
    )
    fun `should return template preview given valid request`(
        sourceType: SourceType,
        notificationChannel: NotificationChannel,
        templateId: String,
        language: Language,
        withReasons: Boolean
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            sourceType = sourceType,
            channel = notificationChannel,
            language = language,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = if (withReasons) listOf(SignatureRejectionReason.IMAGE_MINUS_NOT_MINUS_CLEAR) else emptyList(),
                rejectionNotes = if (withReasons) "Invalid" else null,
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
        val expectedRejectionReasons = if (language == Language.EN) listOf("The image was not clear") else listOf("CY TODO The image was not clear")
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
                "eroPostcode" to eroContactDetails.address.postcode
            )
        }
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL,EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,true,false",
            "POSTAL,LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,true,false",
            "PROXY,EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,true,false",
            "PROXY,LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,true,false",
            "POSTAL,EMAIL,$POSTAL_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,false,true",
            "POSTAL,LETTER,$POSTAL_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,false,true",
            "PROXY,EMAIL,$PROXY_EMAIL_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,false,true",
            "PROXY,LETTER,$PROXY_LETTER_SIGNATURE_WITH_REASONS_ENGLISH_TEMPLATE_ID,false,true"
        ]
    )
    fun `should return template preview given valid request when optional values are not populated`(
        sourceType: SourceType,
        notificationChannel: NotificationChannel,
        templateId: String,
        populateRejectionReasons: Boolean,
        populateRejectionNotes: Boolean
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateRejectedSignatureTemplatePreviewRequest(
            sourceType = sourceType,
            channel = notificationChannel,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionReasons = if (populateRejectionReasons) listOf(SignatureRejectionReason.IMAGE_MINUS_NOT_MINUS_CLEAR) else emptyList(),
                rejectionNotes = if (populateRejectionNotes) "Rejection note" else null,
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull())
            )
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectionReasons" to if (populateRejectionReasons) listOf("The image was not clear") else emptyArray<String>(),
                "rejectionNotes" to if (populateRejectionNotes) "Rejection note" else "",
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
            "No ${notificationChannel.name.lowercase()} template defined in ${language.toMessage()} for notification type ${NotificationType.REJECTED_SIGNATURE_WITH_REASONS} and sourceType $sourceTypeValue"
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
