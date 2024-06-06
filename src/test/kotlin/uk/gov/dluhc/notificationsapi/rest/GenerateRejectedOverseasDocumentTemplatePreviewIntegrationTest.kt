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
import uk.gov.dluhc.notificationsapi.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedOverseasDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedOverseasDocumentTemplatePreviewRequest
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateRejectedOverseasDocumentTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val REJECTED_PARENT_GUARDIAN_EMAIL_EN_TEMPLATE_ID = "90d605be-36bb-4cea-b453-aaf16ca249b3"
        private const val REJECTED_PARENT_GUARDIAN_EMAIL_CY_TEMPLATE_ID = "63b51e19-fdcb-470d-b62a-f96b076bc9ba"
        private const val REJECTED_PARENT_GUARDIAN_LETTER_EN_TEMPLATE_ID = "1d4330c0-d636-4357-8cfe-5317aef17ebb"
        private const val REJECTED_PARENT_GUARDIAN_LETTER_CY_TEMPLATE_ID = "071cda09-08eb-48ff-8fcc-911b7e9ffc03"
        private const val REJECTED_PREVIOUS_ADDRESS_EMAIL_EN_TEMPLATE_ID = "b6406aa6-c762-49a6-912f-7e0437800cec"
        private const val REJECTED_PREVIOUS_ADDRESS_EMAIL_CY_TEMPLATE_ID = "10752429-44ee-429f-85bb-57ade4f62e5f"
        private const val REJECTED_PREVIOUS_ADDRESS_LETTER_EN_TEMPLATE_ID = "8975fe83-7e55-422c-b041-84607336b19e"
        private const val REJECTED_PREVIOUS_ADDRESS_LETTER_CY_TEMPLATE_ID = "23827953-f350-425b-b563-2a7e2835b469"
        private const val REJECTED_DOCUMENT_EMAIL_EN_TEMPLATE_ID = "4c4e0daa-fc8c-49fd-be74-10dccd5de80e"
        private const val REJECTED_DOCUMENT_EMAIL_CY_TEMPLATE_ID = "664ed443-f1a6-48d4-b066-b6c0f1e0953a"
        private const val REJECTED_DOCUMENT_LETTER_EN_TEMPLATE_ID = "ee06830e-25d1-4e54-adbc-aa79d5aef1fc"
        private const val REJECTED_DOCUMENT_LETTER_CY_TEMPLATE_ID = "664ed443-f1a6-48d4-b066-b6c0f1e0953a"
        private const val URI_TEMPLATE = "/templates/rejected-overseas-document/preview"
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
        "$REJECTED_PARENT_GUARDIAN_EMAIL_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN",
        "$REJECTED_PREVIOUS_ADDRESS_EMAIL_EN_TEMPLATE_ID, PREVIOUS_MINUS_ADDRESS",
        "$REJECTED_DOCUMENT_EMAIL_EN_TEMPLATE_ID, IDENTITY",
    )
    fun `should return not found given non existing template`(
        templateId: String,
        documentCategory: DocumentCategory,
    ) {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(
            templateId,
        )

        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(APPLICATION_JSON)
            .withAValidBody(documentCategory)
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "$REJECTED_DOCUMENT_EMAIL_EN_TEMPLATE_ID, IDENTITY, EMAIL, EN",
            "$REJECTED_DOCUMENT_EMAIL_CY_TEMPLATE_ID, IDENTITY, EMAIL, CY",
            "$REJECTED_DOCUMENT_LETTER_EN_TEMPLATE_ID, IDENTITY, LETTER, EN",
            "$REJECTED_DOCUMENT_LETTER_CY_TEMPLATE_ID, IDENTITY, LETTER, CY",
            "$REJECTED_PARENT_GUARDIAN_EMAIL_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, EMAIL, EN",
            "$REJECTED_PARENT_GUARDIAN_EMAIL_CY_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, EMAIL, CY",
            "$REJECTED_PARENT_GUARDIAN_LETTER_EN_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, LETTER, EN",
            "$REJECTED_PARENT_GUARDIAN_LETTER_CY_TEMPLATE_ID, PARENT_MINUS_GUARDIAN, LETTER, CY",
            "$REJECTED_PREVIOUS_ADDRESS_EMAIL_EN_TEMPLATE_ID, PREVIOUS_MINUS_ADDRESS, EMAIL, EN",
            "$REJECTED_PREVIOUS_ADDRESS_EMAIL_CY_TEMPLATE_ID, PREVIOUS_MINUS_ADDRESS, EMAIL, CY",
            "$REJECTED_PREVIOUS_ADDRESS_LETTER_EN_TEMPLATE_ID, PREVIOUS_MINUS_ADDRESS, LETTER, EN",
            "$REJECTED_PREVIOUS_ADDRESS_LETTER_CY_TEMPLATE_ID, PREVIOUS_MINUS_ADDRESS, LETTER, CY",
        ],
    )
    fun `should return template preview given valid json request`(
        templateId: String,
        documentCategory: DocumentCategory,
        channel: NotificationChannel,
        language: Language,
    ) {
        // Given
        val notifyClientResponse =
            NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildRejectedOverseasDocumentTemplatePreviewRequest(
            channel = channel,
            language = language,
            documentCategory = documentCategory,
            personalisation = buildRejectedOverseasDocumentPersonalisation(
                documents = listOf(buildRejectedDocument(rejectionReasons = emptyList(), rejectionNotes = null)),
                applicationReference = "applicationReference",
                eroContactDetails = buildEroContactDetails(
                    localAuthorityName = "Barcelona",
                    address = buildAddress(
                        street = "some street",
                        postcode = "postcode",
                    ),
                ),
            ),
        )

        val expectedDocument = if (language == Language.EN) "Utility bill" else "Bil cyfleustodau"

        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf<String, Any>(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "rejectedDocuments" to listOf(expectedDocument),
                "rejectionMessage" to "",
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
                GenerateRejectedOverseasDocumentTemplatePreviewRequest::class.java,
            )
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(
            templateId,
            expectedPersonalisationDataMap,
        )
    }
}

private fun WebTestClient.RequestBodySpec.withAValidBody(documentCategory: DocumentCategory): WebTestClient.RequestBodySpec =
    body(
        Mono.just(buildRejectedOverseasDocumentTemplatePreviewRequest(documentCategory = documentCategory)),
        GenerateRejectedOverseasDocumentTemplatePreviewRequest::class.java,
    ) as WebTestClient.RequestBodySpec
