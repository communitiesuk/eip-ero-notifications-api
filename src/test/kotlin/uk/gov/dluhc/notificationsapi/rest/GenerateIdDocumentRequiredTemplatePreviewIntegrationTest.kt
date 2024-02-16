package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateIdDocumentRequiredTemplatePreviewRequest
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateIdDocumentRequiredTemplatePreviewIntegrationTest : IntegrationTest() {
    companion object {
        private const val URI_TEMPLATE = "/templates/id-document-required/preview"
        private const val ID_DOCUMENT_REQUIRED_EMAIL_ENGLISH_VAC = "fdd31588-c982-11ed-afa1-0242ac120002"
        private const val ID_DOCUMENT_REQUIRED_EMAIL_WELSH_VAC = "add31fb0-c982-11ed-afa1-0242ac120002"
        private const val ID_DOCUMENT_REQUIRED_LETTER_ENGLISH_VAC = "bdd326c2-c982-11ed-afa1-0242ac120002"
        private const val ID_DOCUMENT_REQUIRED_LETTER_WELSH_VAC = "cdd32cd0-c982-11ed-afa1-0242ac120002"

        private const val ID_DOCUMENT_REQUIRED_EMAIL_ENGLISH_OVERSEAS = "cea6ecec-7627-4322-b220-ee70a69f014d"
        private const val ID_DOCUMENT_REQUIRED_EMAIL_WELSH_OVERSEAS = "1f922de4-1e70-4aaf-9b76-7ab789fe7d1a"
        private const val ID_DOCUMENT_REQUIRED_LETTER_ENGLISH_OVERSEAS = "0305c716-cfbc-401d-b95b-a6aeda741ba6"
        private const val ID_DOCUMENT_REQUIRED_LETTER_WELSH_OVERSEAS = "abd343c5-edab-4e58-82b4-293736a464d0"
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(idDocumentRequiredEmailEnglishTemplateId)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withAValidBody()
            .exchange()
            .expectStatus()
            .isNotFound
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(404)
            .hasError("Not Found")
            .hasMessage("Notification template not found for the given template type")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return bad request given missing request body`() {
        // Given
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining("Required request body is missing")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return bad request given missing request body parameters to gov notify`() {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(idDocumentRequiredEmailEnglishTemplateId)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withAValidBody()
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.assertThat(actual)
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

        val requestBody = """
            {
              "channel": "email",
              "language": "en",
              "sourceType": "voter-card",
              "personalisation": {
                "applicationReference": "",
                "firstName": "",
                "idDocumentRequiredFreeText" : "",
                "eroContactDetails": {
                  "localAuthorityName": "",
                  "website": "",
                  "phone": "",
                  "email": "",
                  "address": {
                    "street": "",
                    "property": "",
                    "locality": "",
                    "town": "",
                    "area": "",
                    "postcode": "PE11111111111"
                  }
                }
              }
            }
        """.trimIndent()

        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val expectedValidationErrorsCount = 4

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bodyValue(requestBody)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        ErrorResponseAssert.assertThat(actual)
            .hasTimestampNotBefore(earliestExpectedTimeStamp)
            .hasStatus(400)
            .hasError("Bad Request")
            .hasMessageContaining("Validation failed for object='generateIdDocumentRequiredTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [PE11111111111], size must be between 1 and 10")
    }

    @ParameterizedTest
    @CsvSource(
        "$ID_DOCUMENT_REQUIRED_EMAIL_ENGLISH_VAC, VOTER_MINUS_CARD, EN, EMAIL",
        "$ID_DOCUMENT_REQUIRED_EMAIL_WELSH_VAC, VOTER_MINUS_CARD, CY, EMAIL",
        "$ID_DOCUMENT_REQUIRED_LETTER_ENGLISH_VAC, VOTER_MINUS_CARD, EN, LETTER",
        "$ID_DOCUMENT_REQUIRED_LETTER_WELSH_VAC, VOTER_MINUS_CARD, CY, LETTER",

        "$ID_DOCUMENT_REQUIRED_EMAIL_ENGLISH_OVERSEAS, OVERSEAS, EN, EMAIL",
        "$ID_DOCUMENT_REQUIRED_EMAIL_WELSH_OVERSEAS, OVERSEAS, CY, EMAIL",
        "$ID_DOCUMENT_REQUIRED_LETTER_ENGLISH_OVERSEAS, OVERSEAS, EN, LETTER",
        "$ID_DOCUMENT_REQUIRED_LETTER_WELSH_OVERSEAS, OVERSEAS, CY, LETTER",
    )
    fun `should return letter template preview given valid request`(
        templateId: String,
        sourceType: SourceType,
        language: Language,
        channel: NotificationChannel
    ) {
        // Given
        val notifyClientResponse =
            NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateIdDocumentRequiredTemplatePreviewRequest(
            language = language,
            channel = channel,
            sourceType = sourceType
        )
        val expectedPersonalisationDataMap = getExpectedPersonalisationMap(requestBody.personalisation)
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GeneratePhotoResubmissionTemplatePreviewRequest::class.java
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    private fun getExpectedPersonalisationMap(personalisation: IdDocumentRequiredPersonalisation): Map<String, Any> =
        with(personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "ninoFailFreeText" to idDocumentRequiredFreeText,
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to (eroContactDetails.address.property ?: ""),
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to (eroContactDetails.address.town ?: ""),
                "eroAddressLine4" to (eroContactDetails.address.area ?: ""),
                "eroAddressLine5" to (eroContactDetails.address.locality ?: ""),
                "eroPostcode" to eroContactDetails.address.postcode
            )
        }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        body(
            Mono.just(buildGenerateIdDocumentRequiredTemplatePreviewRequest()),
            GenerateIdDocumentRequiredTemplatePreviewRequest::class.java
        ) as WebTestClient.RequestBodySpec
}
