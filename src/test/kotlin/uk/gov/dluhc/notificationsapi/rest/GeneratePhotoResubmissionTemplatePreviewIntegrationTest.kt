package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildPhotoResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.MILLIS

internal class GeneratePhotoResubmissionTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val PHOTO_TEMPLATE_ID = "598f9383-06c9-4dcb-ac53-2583f3845d9b"
        private const val URI_TEMPLATE = "/templates/photo-resubmission/preview"
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(PHOTO_TEMPLATE_ID)
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
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(PHOTO_TEMPLATE_ID)
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

        val requestBody = """
            {
              "channel": "email",
              "language": "en",
              "sourceType": "voter-card",
              "personalisation": {
                "applicationReference": "",
                "firstName": "",
                "photoRejectionReasons": [],
                "photoRequestFreeText": "",
                "uploadPhotoLink": "",
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
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)
        val expectedValidationErrorsCount = 4

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bodyValue(requestBody)
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
            .hasMessageContaining("Validation failed for object='generatePhotoResubmissionTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [PE11111111111], size must be between 1 and 10")
    }

    @Test
    fun `should return template preview given valid json request`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = PHOTO_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = """
            {
              "channel": "email",
              "language": "en",
              "sourceType": "voter-card",
              "personalisation": {
                "applicationReference": "A3JSZC4CRH",
                "firstName": "Fred",
                "photoRejectionReasons": ["other-objects-or-people-in-photo"],
                "photoRequestFreeText": "Please provide a clear image",
                "uploadPhotoLink": "photo-398c1be2-7950-48a2-aca8-14cb9276a673",
                "eroContactDetails": {
                  "localAuthorityName": "City of Sunderland",
                  "website": "ero-address.com",
                  "phone": "01234 567890",
                  "email": "fred.blogs@some-domain.co.uk",
                  "address": {
                    "street": "Charles Lane Street",
                    "property": "Some Property",
                    "locality": "Some locality",
                    "town": "London",
                    "area": "Charles Area",
                    "postcode": "PE3 6SB"
                  }
                }
              }
            }
        """.trimIndent()

        val expectedPersonalisationDataMap = mapOf(
            "applicationReference" to "A3JSZC4CRH",
            "firstName" to "Fred",
            "photoRejectionReasons" to listOf("There are other people or objects in the photo"),
            "photoRejectionNotes" to "",
            "photoRequestFreeText" to "Please provide a clear image",
            "uploadPhotoLink" to "photo-398c1be2-7950-48a2-aca8-14cb9276a673",
            "LAName" to "City of Sunderland",
            "eroWebsite" to "ero-address.com",
            "eroEmail" to "fred.blogs@some-domain.co.uk",
            "eroPhone" to "01234 567890",
            "eroAddressLine1" to "Some Property",
            "eroAddressLine2" to "Charles Lane Street",
            "eroAddressLine3" to "London",
            "eroAddressLine4" to "Charles Area",
            "eroAddressLine5" to "Some locality",
            "eroPostcode" to "PE3 6SB",
        )
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
        wireMockService.verifyNotifyGenerateTemplatePreview(PHOTO_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request with all values populated`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = PHOTO_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGeneratePhotoResubmissionTemplatePreviewRequest(sourceType = SourceType.VOTER_MINUS_CARD)
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "photoRejectionReasons" to listOf("There are other people or objects in the photo"),
                "photoRejectionNotes" to (photoRejectionNotes ?: ""),
                "photoRequestFreeText" to photoRequestFreeText,
                "uploadPhotoLink" to uploadPhotoLink,
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
            )
        }
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GeneratePhotoResubmissionTemplatePreviewRequest::class.java,
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(PHOTO_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request when optional values not populated`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = PHOTO_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            personalisation = buildPhotoResubmissionPersonalisationRequest(eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull())),
            sourceType = SourceType.VOTER_MINUS_CARD,
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "photoRejectionReasons" to listOf("There are other people or objects in the photo"),
                "photoRejectionNotes" to (photoRejectionNotes ?: ""),
                "photoRequestFreeText" to photoRequestFreeText,
                "uploadPhotoLink" to uploadPhotoLink,
                "LAName" to eroContactDetails.localAuthorityName,
                "eroWebsite" to eroContactDetails.website,
                "eroEmail" to eroContactDetails.email,
                "eroPhone" to eroContactDetails.phone,
                "eroAddressLine1" to "",
                "eroAddressLine2" to eroContactDetails.address.street,
                "eroAddressLine3" to "",
                "eroAddressLine4" to "",
                "eroAddressLine5" to "",
                "eroPostcode" to eroContactDetails.address.postcode,
            )
        }

        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GeneratePhotoResubmissionTemplatePreviewRequest::class.java,
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(PHOTO_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        body(
            Mono.just(buildGeneratePhotoResubmissionTemplatePreviewRequest(sourceType = SourceType.VOTER_MINUS_CARD)),
            GeneratePhotoResubmissionTemplatePreviewRequest::class.java,
        ) as WebTestClient.RequestBodySpec
}
