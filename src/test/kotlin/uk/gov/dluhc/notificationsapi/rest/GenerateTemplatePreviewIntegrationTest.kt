package uk.gov.dluhc.notificationsapi.rest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_APPROVED
import uk.gov.dluhc.notificationsapi.models.TemplateType.APPLICATION_MINUS_RECEIVED
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN

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

    private fun buildUri(templateType: String) =
        UriComponentsBuilder.fromUriString("/templates/{templateType}/preview").buildAndExpand(templateType)
            .toUriString()
}
