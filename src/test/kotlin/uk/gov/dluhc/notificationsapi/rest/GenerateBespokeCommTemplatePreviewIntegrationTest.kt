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
import uk.gov.dluhc.notificationsapi.mapper.DeadlineMapper
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateBespokeCommTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.annotations.SourceTypesApiEnumTest
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.models.ErrorResponseAssert
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildBespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildContactDetailsRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateBespokeCommTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateBespokeCommTemplatePreviewIntegrationTest : IntegrationTest() {
    companion object {
        private const val EMAIL_ENGLISH_TEMPLATE_ID = "d611c268-d512-4dc8-afa3-c3a833fd21c9"
        private const val LETTER_ENGLISH_TEMPLATE_ID = "a09fca4a-dfc6-47ba-9ecc-ca5f1d580a34"
        private const val EMAIL_WELSH_TEMPLATE_ID = "726ecf58-5974-42d9-bcde-b5f0b3a973f9"
        private const val LETTER_WELSH_TEMPLATE_ID = "41516dc0-8c27-45b5-8b82-bf4b27662715"
        private const val URI_TEMPLATE = "/templates/bespoke-comm/preview"
    }

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    private lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    private lateinit var languageMapper: LanguageMapper

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

    @SourceTypesApiEnumTest
    fun `should return bad request given invalid request with invalid fields`(sourceType: SourceType) {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()

        val requestBody = buildGenerateBespokeCommTemplatePreviewRequest(
            sourceType = sourceType,
            personalisation = buildBespokeCommPersonalisation(
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
            .hasMessageContaining("Validation failed for object='generateBespokeCommTemplatePreviewRequest'. Error count: $expectedValidationErrorsCount")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], must match \".*[a-zA-Z]+.*\"")
            .hasValidationError("Error on field 'personalisation.firstName': rejected value [], size must be between 1 and 255") // also validates size when firstName is blank
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.street': rejected value [], size must be between 1 and 255")
            .hasValidationError("Error on field 'personalisation.eroContactDetails.address.postcode': rejected value [AB11111111111], size must be between 1 and 10")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,postal vote",
            "POSTAL, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,postal vote",
            "POSTAL, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,bleidlais bost",
            "POSTAL, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,bleidlais bost",
            "PROXY, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,proxy vote",
            "PROXY, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,proxy vote",
            "PROXY, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,bleidlais drwy ddirprwy",
            "PROXY, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,bleidlais drwy ddirprwy",
            "OVERSEAS, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,overseas vote",
            "OVERSEAS, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,overseas vote",
            "OVERSEAS, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,bleidlais dramor",
            "OVERSEAS, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,bleidlais dramor",
            "VOTER_MINUS_CARD, EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID,EN,Voter Authority Certificate",
            "VOTER_MINUS_CARD, LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID,EN,Voter Authority Certificate",
            "VOTER_MINUS_CARD, EMAIL, false, $EMAIL_WELSH_TEMPLATE_ID,CY,Dystysgrif Awdurdod Pleidleisiwr",
            "VOTER_MINUS_CARD, LETTER, false, $LETTER_WELSH_TEMPLATE_ID,CY,Dystysgrif Awdurdod Pleidleisiwr",
        ],
    )
    fun `should return template preview given valid request`(
        sourceType: SourceType,
        communicationChannel: CommunicationChannel,
        hasRestrictedDocumentsList: Boolean,
        templateId: String,
        language: Language,
        expectedPersonalisationFullSourceType: String,
    ) {
        // Given
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(notifyClientResponse)
        val requestBody = buildGenerateBespokeCommTemplatePreviewRequest(
            sourceType = sourceType,
            channel = communicationChannel,
            language = language,
            personalisation = buildBespokeCommPersonalisation(
                subjectHeader = "Subject header",
                details = "Details",
                whatToDo = "What to do",
                deadlineDate = LocalDate.now().plusMonths(3),
                deadlineTime = "17:00",
            ),
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
        val languageDto = languageMapper.fromApiToDto(language)
        val expectedWhatYouNeedToDo = requestBody.personalisation.whatToDo != null
        val expectedDeadline = deadlineMapper.toDeadlineString(requestBody.personalisation.deadlineDate!!, requestBody.personalisation.deadlineTime!!, languageDto, sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto))
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf<String, Any>(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "subjectHeader" to requestBody.personalisation.subjectHeader,
                "giveDetailsFreeText" to requestBody.personalisation.details,
                "whatYouNeedToDo" to expectedWhatYouNeedToDo,
                "explainFreeText" to requestBody.personalisation.whatToDo!!,
                "deadline" to expectedDeadline,
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
                "sourceType" to expectedPersonalisationFullSourceType,
            )
        }
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, expectedPersonalisationDataMap)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, false, $EMAIL_ENGLISH_TEMPLATE_ID",
            "LETTER, false, $LETTER_ENGLISH_TEMPLATE_ID",
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

        val requestBody = buildGenerateBespokeCommTemplatePreviewRequest(
            channel = communicationChannel,
            personalisation = buildBespokeCommPersonalisation(
                whatToDo = null,
                deadlineTime = null,
                deadlineDate = null,
                eroContactDetails = buildContactDetailsRequest(address = buildAddressRequestWithOptionalParamsNull()),
            ),
        )
        val expectedPersonalisationDataMap = with(requestBody.personalisation) {
            mapOf(
                "applicationReference" to applicationReference,
                "firstName" to firstName,
                "subjectHeader" to requestBody.personalisation.subjectHeader,
                "giveDetailsFreeText" to requestBody.personalisation.details,
                "whatYouNeedToDo" to false,
                "explainFreeText" to "",
                "deadline" to "",
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
        withABody(buildGenerateBespokeCommTemplatePreviewRequest(sourceType = sourceType))

    private fun WebTestClient.RequestBodySpec.withABody(request: GenerateBespokeCommTemplatePreviewRequest): WebTestClient.RequestBodySpec {
        return body(
            Mono.just(request),
            GenerateBespokeCommTemplatePreviewRequest::class.java,
        ) as WebTestClient.RequestBodySpec
    }
}
