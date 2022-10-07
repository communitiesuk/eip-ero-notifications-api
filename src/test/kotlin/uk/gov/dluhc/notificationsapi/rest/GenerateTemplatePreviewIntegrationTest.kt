package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.ErrorResponse
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_APPROVED
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_RECEIVED
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.ErrorResponseAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

internal class GenerateTemplatePreviewIntegrationTest : IntegrationTest() {
    @BeforeEach
    fun setup() {
        wireMockService.stubCognitoJwtIssuerResponse()
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.post()
            .uri(buildUri(APPLICATION_MINUS_RECEIVED.value))
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return forbidden given no bearer token`() {
        webTestClient.post()
            .uri(buildUri(APPLICATION_MINUS_APPROVED.value))
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return bad request given invalid template type`() {
        // Given
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(buildUri("invalid-template"))
            .withAValidBody()
            .bearerToken(getBearerToken())
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
            .hasMessage("Error on templateType path value: rejected value [invalid-template]")
            .hasNoValidationErrors()
    }

    @Test
    fun `should return bad request given invalid request`() {
        // Given
        val earliestExpectedTimeStamp = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS)

        // When
        val response = webTestClient.post()
            .uri(buildUri())
            .bearerToken(getBearerToken())
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
    fun `should return template preview given valid template type and request`() {
        // Given
        val templateId = "f1571006-c3a0-4c97-884a-189f5b103f85"
        val notifyClientResponse = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
        wireMockService.stubNotifyGenerateTemplatePreviewResponse(notifyClientResponse)
        val personalisation = mapOf(
            "subject_param" to "test subject",
            "name_param" to "John",
            "custom_title" to "Resubmitting photo",
        )
        val expected = with(notifyClientResponse) { GenerateTemplatePreviewResponse(body, subject, html) }

        // When
        val response = webTestClient.post()
            .uri(buildUri(TemplateType.PHOTO_MINUS_RESUBMISSION.value))
            .bearerToken(getBearerToken())
            .body(
                Mono.just(GenerateTemplatePreviewRequest(personalisation = personalisation)),
                GenerateTemplatePreviewRequest::class.java
            )
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(GenerateTemplatePreviewResponse::class.java).responseBody.blockFirst()
        Assertions.assertThat(actual).isEqualTo(expected)
        wireMockService.verifyNotifyGenerateTemplatePreview(templateId, personalisation)
    }

    private fun buildUri(templateType: String = "photo-resubmission") =
        UriComponentsBuilder.fromUriString("/templates/{templateType}/preview").buildAndExpand(templateType)
            .toUriString()

    private fun WebTestClient.RequestBodySpec.withAValidBody(): WebTestClient.RequestBodySpec =
        body(
            Mono.just(GenerateTemplatePreviewRequest(personalisation = mapOf())),
            GenerateTemplatePreviewRequest::class.java
        ) as WebTestClient.RequestBodySpec
}
