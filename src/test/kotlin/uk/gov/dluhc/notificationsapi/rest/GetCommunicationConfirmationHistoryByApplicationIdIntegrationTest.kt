package uk.gov.dluhc.notificationsapi.rest

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerTokenWithAllRolesExcept
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getVCAnonymousAdminBearerToken

internal class GetCommunicationConfirmationHistoryByApplicationIdIntegrationTest : IntegrationTest() {

    companion object {
        private const val URI_TEMPLATE = "/eros/{ERO_ID}/communications/anonymous-applications/{APPLICATION_ID}"
        private const val APPLICATION_ID = "7762ccac7c056046b75d4aa3"
    }

    @Test
    fun `should return unauthorized given no bearer token`() {
        webTestClient.get()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.get()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different group`() {
        wireMockService.stubCognitoJwtIssuerResponse()

        webTestClient.get()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            // the group ero-vc-anonymous-admin-$ERO_ID is required to be successful
            .bearerToken(
                getBearerTokenWithAllRolesExcept(eroId = ERO_ID, excludedRoles = listOf("ero-vc-anonymous-admin"))
            )
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different ero`() {
        wireMockService.stubCognitoJwtIssuerResponse()

        webTestClient.get()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            // the group ero-vc-anonymous-admin-$ERO_ID is required to be successful
            .bearerToken(getVCAnonymousAdminBearerToken(OTHER_ERO_ID))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden
    }
}
