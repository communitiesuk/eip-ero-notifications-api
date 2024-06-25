package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.annotations.AllowedSourceTypesTest
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateNinoNotMatchedTemplatePreviewIntegrationTest : IntegrationTest() {

    companion object {
        private const val EMAIL_ENGLISH_TEMPLATE_ID = "8fa64777-222f-45e9-937b-6236359b79df"
        private const val LETTER_ENGLISH_TEMPLATE_ID = "b934fb1a-a199-41cc-829d-bf025ad1b740"
        private const val EMAIL_WELSH_TEMPLATE_ID = "8fa64777-222f-45e9-937b-6236359b79df"
        private const val LETTER_WELSH_TEMPLATE_ID = "b934fb1a-a199-41cc-829d-bf025ad1b740"
        private const val RESTRICTED_DOCUMENTS_LIST_EMAIL_ENGLISH_TEMPLATE_ID = "6884a755-9e83-4d5b-8a21-ed547269ccbe"
        private const val RESTRICTED_DOCUMENTS_LIST_LETTER_ENGLISH_TEMPLATE_ID = "3178650d-460f-47fd-aeee-14a2c8e6a985"
        private const val RESTRICTED_DOCUMENTS_LIST_EMAIL_WELSH_TEMPLATE_ID = "caf616da-c989-4cfb-9392-3f5e78f424e2"
        private const val RESTRICTED_DOCUMENTS_LIST_LETTER_WELSH_TEMPLATE_ID = "bcd3467e-5bc7-4689-9c9b-25a020ca1bca"
        private const val URI_TEMPLATE = "/templates/nino-not-matched/preview"
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
        wireMockService.stubNotifyGenerateTemplatePreviewNotFoundResponse(EMAIL_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withAValidBody(SourceType.POSTAL)
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
    fun `should return bad request given valid missing request body parameters to gov notify`() {
        // Given
        wireMockService.stubNotifyGenerateTemplatePreviewBadRequestResponse(EMAIL_ENGLISH_TEMPLATE_ID)
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withAValidBody(SourceType.POSTAL)
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

    @AllowedSourceTypesTest
    fun `should return bad request given invalid request with invalid fields`(sourceType: SourceType) {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()

        val requestBody = buildGenerateNinoNotMatchedTemplatePreviewRequest(
            sourceType = sourceType,
            personalisation = buildNinoNotMatchedPersonalisation(
                applicationReference = "",
                firstName = "",
                eroContactDetails = buildEroContactDetails(
                    address = buildAddress(
                        street = "",
                        postcode = "AB11111111111",
                    ),
                ),
            ),
        )
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val expectedValidationErrorsCount = 4

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withABody(requestBody)
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
            .hasMessageContaining("Validation failed for object='generateNinoNotMatchedTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [AB11111111111], size must be between 1 and 10")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,postal",
            "POSTAL, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,postal",
            "POSTAL, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,drwy'r post",
            "POSTAL, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,drwy'r post",
            "PROXY, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,proxy",
            "PROXY, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,proxy",
            "PROXY, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,drwy ddirprwy",
            "PROXY, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,drwy ddirprwy",
            "POSTAL, EMAIL, true, $RESTRICTED_DOCUMENTS_LIST_EMAIL_ENGLISH_TEMPLATE_ID,EN,postal",
            "POSTAL, LETTER, true, $RESTRICTED_DOCUMENTS_LIST_LETTER_ENGLISH_TEMPLATE_ID,EN,postal",
            "POSTAL, EMAIL, true, $RESTRICTED_DOCUMENTS_LIST_EMAIL_WELSH_TEMPLATE_ID,CY,drwy'r post",
            "POSTAL, LETTER, true, $RESTRICTED_DOCUMENTS_LIST_LETTER_WELSH_TEMPLATE_ID,CY,drwy'r post",
            "PROXY, EMAIL, true, $RESTRICTED_DOCUMENTS_LIST_EMAIL_ENGLISH_TEMPLATE_ID,EN,proxy",
            "PROXY, LETTER, true, $RESTRICTED_DOCUMENTS_LIST_LETTER_ENGLISH_TEMPLATE_ID,EN,proxy",
            "PROXY, EMAIL, true, $RESTRICTED_DOCUMENTS_LIST_EMAIL_WELSH_TEMPLATE_ID,CY,drwy ddirprwy",
            "PROXY, LETTER, true, $RESTRICTED_DOCUMENTS_LIST_LETTER_WELSH_TEMPLATE_ID,CY,drwy ddirprwy",
        ],
    )
    fun `should return template preview given valid request`(
        sourceType: SourceType,
        CommunicationChannel: CommunicationChannel,
        hasRestrictedDocumentsList: Boolean,
        templateId: String,
        language: Language,
        expectedPersonalisationSourceType: String,
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateNinoNotMatchedTemplatePreviewRequest(
            sourceType = sourceType,
            channel = CommunicationChannel,
            language = language,
            personalisation = buildNinoNotMatchedPersonalisation(
                additionalNotes = "Invalid",
            ),
            hasRestrictedDocumentsList = hasRestrictedDocumentsList,
        )

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actualResponse = response.responseBody.blockFirst()
        val expectedResponse = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }
        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse)
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf<String, Any>(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "additionalNotes" to requestBody.personalisation.additionalNotes!!,
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
                "sourceType" to expectedPersonalisationSourceType,
            )
        }
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID",
            "LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID",
            "EMAIL, true, $RESTRICTED_DOCUMENTS_LIST_EMAIL_ENGLISH_TEMPLATE_ID",
            "LETTER, true, $RESTRICTED_DOCUMENTS_LIST_LETTER_ENGLISH_TEMPLATE_ID",
        ],
    )
    fun `should return template preview given valid request when optional values are not populated`(
        communicationChannel: CommunicationChannel,
        hasRestrictedDocumentsList: Boolean,
        templateId: String,
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)

        val requestBody = buildGenerateNinoNotMatchedTemplatePreviewRequest(
            channel = communicationChannel,
            personalisation = buildNinoNotMatchedPersonalisation(
                additionalNotes = null,
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull()),
            ),
            hasRestrictedDocumentsList = hasRestrictedDocumentsList,
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "additionalNotes" to "",
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
                "sourceType" to "postal",
            )
        }

        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(URI_TEMPLATE)
            .bearerToken(getBearerToken())
            .contentType(MediaType.APPLICATION_JSON)
            .withABody(requestBody)
            .exchange()
            .expectStatus().isOk
            .returnResult(GenerateTemplatePreviewResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    private fun WebTestClient.RequestBodySpec.withAValidBody(sourceType: SourceType): WebTestClient.RequestBodySpec =
        withABody(buildGenerateNinoNotMatchedTemplatePreviewRequest(sourceType = sourceType))

    private fun WebTestClient.RequestBodySpec.withABody(request: GenerateNinoNotMatchedTemplatePreviewRequest): WebTestClient.RequestBodySpec {
        return body(
            Mono.just(request),
            GenerateNinoNotMatchedTemplatePreviewRequest::class.java,
        ) as WebTestClient.RequestBodySpec
    }
}
