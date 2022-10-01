package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.buildAccessToken

// TODO - delete this test when we implement controllers and their tests for stories
internal class SecurityControllerIntegrationTest : IntegrationTest() {

    @Test
    fun `should return principal name given request with bearer token`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()

        val request = webTestClient.get()
            .uri("/secured-endpoint")
            .bearerToken("Bearer ${buildAccessToken(email = "an-ero-user@a-council.gov.uk")}")

        // When
        val response = request.exchange()

        // Then
        val responseBody = response
            .expectStatus().isOk
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        assertThat(responseBody).isEqualTo("Hello an-ero-user@a-council.gov.uk")
    }

    @Test
    fun `should return unauthorized given no bearer token`() {
        // Given
        val request = webTestClient.get().uri("/secured-endpoint")

        // When
        val response = request.exchange()

        // Then
        response.expectStatus().isUnauthorized
    }
}
