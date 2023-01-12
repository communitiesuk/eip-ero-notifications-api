package uk.gov.dluhc.notificationsapi.rest

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.getDifferentRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.getRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import java.util.UUID

internal class GetCommunicationHistoryByApplicationIdIntegrationTest : IntegrationTest() {

    companion object {
        private val ERO_ID = getRandomEroId()
        private val OTHER_ERO_ID = getDifferentRandomEroId(ERO_ID)
    }

    @Test
    fun `should return unauthorized given no bearer token`() {
        webTestClient.get()
            .uri(buildUri())
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.get()
            .uri(buildUri())
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different group`() {
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroId = ERO_ID

        webTestClient.get()
            .uri(buildUri(eroId = eroId))
            .bearerToken(getBearerToken(eroId = eroId, groups = listOf("ero-$eroId", "ero-admin-$eroId")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different ero`() {
        wireMockService.stubCognitoJwtIssuerResponse()
        val requestEroId = ERO_ID
        val userGroupEroId = OTHER_ERO_ID

        webTestClient.get()
            .uri(buildUri(eroId = requestEroId))
            .bearerToken(getBearerToken(eroId = userGroupEroId, groups = listOf("ero-$userGroupEroId", "ero-vc-admin-$userGroupEroId")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden
    }

    private fun buildUri(eroId: String = ERO_ID, applicationId: String = UUID.randomUUID().toString()) =
        "/eros/$eroId/communications/applications/$applicationId"
}
