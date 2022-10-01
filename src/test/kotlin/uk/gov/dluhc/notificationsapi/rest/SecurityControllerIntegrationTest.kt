package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken

// TODO - delete this test when we implement controllers and their tests for stories
internal class SecurityControllerIntegrationTest : IntegrationTest() {

    companion object {
        private const val BEARER_TOKEN: String =
            "Bearer eyJraWQiOiIvd1Jvb2Q3Y29GODVvLzVNbldDWlBDM1BkOFBWRDZGaFQ2QlZDWGdTL1AwPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiUTVHRUFEcWNoR3RlZ1g5MDRQS0UyQSIsInN1YiI6IjVhMTAwMTk4LWRmNWItNDBmZS1hMjIwLTRhMzJiNmNlMGY3YSIsImNvZ25pdG86Z3JvdXBzIjpbImVyby12Yy1hZG1pbi1jYW1kZW4tbG9uZG9uLWJvcm91Z2gtY291bmNpbCIsImVyby1jYW1kZW4tbG9uZG9uLWJvcm91Z2gtY291bmNpbCIsImVyby1hZG1pbi1jYW1kZW4tbG9uZG9uLWJvcm91Z2gtY291bmNpbCJdLCJpc3MiOiJodHRwczovL2NvZ25pdG8taWRwLmV1LXdlc3QtMi5hbWF6b25hd3MuY29tL2V1LXdlc3QtMl8xMjN0NXhBYmMiLCJjb2duaXRvOnVzZXJuYW1lIjoiMmMzMmZiNGItNzMxOC00ZWE5LWI4YzctNjIxMjQ3NDFjNzhkIiwib3JpZ2luX2p0aSI6IjllOTljNDlhLThhZDUtNGQ3Yy05Y2UxLTEzZDYyYzIzOGFiYiIsImF1ZCI6IjJsc2kzcDQ2YXBuaDRlMjBlOHQybHRkbXQ5IiwiZXZlbnRfaWQiOiI0NjQ4ZmRlMC04NWViLTRhY2ItOWZhOC02NjA5ZWY2NDUxZGIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTY2MTg1MjcwMSwicGhvbmVfbnVtYmVyIjoiKzQ0Nzg5MDc2NTQxMSIsImlhdCI6MTY2MTg1MjczNCwianRpIjoiMjI3Y2FmNGEtMjFjOS00NTI4LThkZDctYmE4Y2UwYzY5YTQ0IiwiZW1haWwiOiJhbi1lcm8tdXNlckBhLWNvdW5jaWwuZ292LnVrIn0.Ne-QycvW9kElO7SDG5Wxa7BtXG9qkE1Nld60f6urvm8u_G17CXQofjePdIR4Gkkjrog8OL4zCzzTMh7rqzLPsrYZE7CCpFdr7Wki56RbQ1uh_rYxgqVMMntwfxZvpsQU3WXJTR5IUTOCr4_iw8KWV8rCMb2yIEn7SXGMZt_J1sY"
    }

    @Test
    fun `should return principal name given request with bearer token`() {
        // Given
        val request = webTestClient.get()
            .uri("/secured-endpoint")
            .bearerToken(BEARER_TOKEN)

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
