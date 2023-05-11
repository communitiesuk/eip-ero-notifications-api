package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.NotificationChannel.LETTER
import uk.gov.dluhc.notificationsapi.models.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocumentPersonalisation
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit.MILLIS

internal class GenerateRejectedDocumentTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val EMAIL_DOCUMENT_TEMPLATE_ID = "74ba448b-dce8-434a-baff-968ac3d19657"
        private const val LETTER_DOCUMENT_TEMPLATE_ID = "ab3c859b-93df-4f47-bc80-c158acc6aac1"
        private const val URI_TEMPLATE = "/templates/rejected-document/preview"
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(EMAIL_DOCUMENT_TEMPLATE_ID)
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
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(EMAIL_DOCUMENT_TEMPLATE_ID)
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

        val requestBody = buildGenerateRejectedDocumentTemplatePreviewRequest(
            sourceType = POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(
                applicationReference = "",
                firstName = "",
                documents = emptyList(),
                eroContactDetails = buildEroContactDetails(address = buildAddress(street = "", postcode = "PE11111111111"))
            )
        )
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(MILLIS)
        val expectedValidationErrorsCount = 5

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
            .hasMessageContaining("Validation failed for object='generateRejectedDocumentTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.documents': rejected value [[]], size must be between 1 and 2147483647")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [PE11111111111], size must be between 1 and 10")
    }

    @Test
    fun `should return template preview given valid json request for email`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = EMAIL_DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateRejectedDocumentTemplatePreviewRequest(
            sourceType = POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(buildRejectedDocument(rejectionNotes = "Some notes here"))
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
                "rejectedDocuments" to listOf("Utility bill - The document is too old - Some notes here"),
                "rejectionMessage" to rejectedDocumentFreeText!!,
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
        wireMockService.verifyNotifyGenerateTemplatePreview(EMAIL_DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request for letter`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = LETTER_DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateRejectedDocumentTemplatePreviewRequest(
            sourceType = POSTAL,
            channel = LETTER,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(
                    buildRejectedDocument(rejectionNotes = "Notes for letter")
                )
            )
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectedDocuments" to listOf("Utility bill - The document is too old - Notes for letter"),
                "rejectionMessage" to rejectedDocumentFreeText!!,
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
        assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(LETTER_DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    @Test
    fun `should return template preview given valid request when optional values not populated`() {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = EMAIL_DOCUMENT_TEMPLATE_ID)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateRejectedDocumentTemplatePreviewRequest(
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(buildRejectedDocument(rejectionReason = null, rejectionNotes = null)),
                rejectedDocumentFreeText = null,
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull())
            ),
            sourceType = POSTAL
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectedDocuments" to listOf("Utility bill"),
                "rejectionMessage" to "",
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
        assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(EMAIL_DOCUMENT_TEMPLATE_ID, expectedPersonalisationDataMap)
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        withABody(buildGenerateRejectedDocumentTemplatePreviewRequest(sourceType = POSTAL))

    private fun WebTestClient.RequestBodySpec.withABody(request: GenerateRejectedDocumentTemplatePreviewRequest): WebTestClient.RequestBodySpec {
        return body(
            Mono.just(request),
            GenerateRejectedDocumentTemplatePreviewRequest::class.java
        ) as WebTestClient.RequestBodySpec
    }
}
