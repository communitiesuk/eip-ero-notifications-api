package uk.gov.dluhc.notificationsapi.rest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_APPROVED
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_RECEIVED
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken

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
        webTestClient.post()
            .uri(buildUri("invalid-template"))
            .withAValidBody()
            .bearerToken(getBearerToken())
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return bad request given invalid request`() {
        webTestClient.post()
            .uri(buildUri())
            .bearerToken(getBearerToken())
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return ok given valid template type and request`() {
        webTestClient.post()
            .uri(buildUri())
            .bearerToken(getBearerToken())
            .withAValidBody()
            .exchange()
            .expectStatus()
            .isOk
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
