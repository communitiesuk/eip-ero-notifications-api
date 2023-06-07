package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.models.DocumentType
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildIdDocumentResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.MILLIS
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

internal class GenerateIdDocumentResubmissionTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val DOCUMENT_TEMPLATE_ID = "bd30cf0b-751b-4590-bf7c-b3a6a3b3b87e"
        private const val URI_TEMPLATE = "/templates/id-document-resubmission/preview"
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(DOCUMENT_TEMPLATE_ID)
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
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(DOCUMENT_TEMPLATE_ID)
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
                "idDocumentRequestFreeText": "",
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
            .hasMessageContaining("Validation failed for object='generateIdDocumentResubmissionTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [PE11111111111], size must be between 1 and 10")
    }

    @Test
    fun `should return template preview given valid json request`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = """
            {
              "channel": "email",
              "language": "en",
              "sourceType": "voter-card",
              "personalisation": {
                "applicationReference": "A3JSZC4CRH",
                "firstName": "Fred",
                "idDocumentRequestFreeText": "Please provide valid identity document",
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
                },
                "rejectedDocuments": [
                    {
                        "documentType": "birth-certificate",
                        "rejectionReasons": ["unreadable-document"]
                    }
                ]
              }
            }
        """.trimIndent()

        val expectedPersonalisationDataMap = mutableMapOf(
            "applicationReference" to "A3JSZC4CRH",
            "firstName" to "Fred",
            "documentRequestFreeText" to "Please provide valid identity document",
            "documentRejectionText" to "Birth certificate\n\n* We were unable to read the document provided because it was not clear or not showing the information we needed\n\n----\n\n",
            "LAName" to "City of Sunderland",
            "eroWebsite" to "ero-address.com",
            "eroEmail" to "fred.blogs@some-domain.co.uk",
            "eroPhone" to "01234 567890",
            "eroAddressLine1" to "Some Property",
            "eroAddressLine2" to "Charles Lane Street",
            "eroAddressLine3" to "London",
            "eroAddressLine4" to "Charles Area",
            "eroAddressLine5" to "Some locality",
            "eroPostcode" to "PE3 6SB"
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
        wireMockService.verifyNotifyGenerateTemplatePreview(DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request with all values populated`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentType.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT),
                        rejectionNotes = "I can't read it"
                    )
                )
            )
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "documentRequestFreeText" to idDocumentRequestFreeText,
                "documentRejectionText" to "Birth certificate\n\n* We were unable to read the document provided because it was not clear or not showing the information we needed\n\nI can't read it\n\n----\n\n",
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
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .body(
                Mono.just(requestBody),
                GenerateIdDocumentResubmissionTemplatePreviewRequest::class.java
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request when optional values not populated`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull()),
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        documentType = DocumentType.BIRTH_MINUS_CERTIFICATE,
                        rejectionReasons = listOf(DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT),
                        rejectionNotes = null
                    )
                )
            ),
            sourceType = SourceTypeModel.VOTER_MINUS_CARD
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "documentRequestFreeText" to idDocumentRequestFreeText,
                "documentRejectionText" to "Birth certificate\n\n* We were unable to read the document provided because it was not clear or not showing the information we needed\n\n----\n\n",
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
            .body(
                Mono.just(requestBody),
                GenerateIdDocumentResubmissionTemplatePreviewRequest::class.java
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        body(
            Mono.just(buildGenerateIdDocumentResubmissionTemplatePreviewRequest(sourceType = SourceTypeModel.VOTER_MINUS_CARD)),
            GenerateIdDocumentResubmissionTemplatePreviewRequest::class.java
        ) as WebTestClient.RequestBodySpec
}
