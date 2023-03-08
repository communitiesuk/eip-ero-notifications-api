package uk.gov.dluhc.notificationsapi.rest

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.getRandomGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerTokenWithAllRolesExcept
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getVCAnonymousAdminBearerToken

internal class CreateOfflineCommunicationConfirmationIntegrationTest : IntegrationTest() {

    companion object {
        private const val URI_TEMPLATE = "/eros/{ERO_ID}/communications/anonymous-applications/{APPLICATION_ID}"
        private const val APPLICATION_ID = "7762ccac7c056046b75d4aa3"
    }

    @Test
    fun `should return forbidden given no bearer token`() {
        webTestClient.post()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                buildBody(),
                CreateOfflineCommunicationConfirmationRequest::class.java
            )
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return unauthorized given user with invalid bearer token`() {
        webTestClient.post()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            .bearerToken(UNAUTHORIZED_BEARER_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                buildBody(),
                CreateOfflineCommunicationConfirmationRequest::class.java
            )
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different group`() {
        wireMockService.stubCognitoJwtIssuerResponse()

        webTestClient.post()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            // the group ero-vc-anonymous-admin-$ERO_ID is required to be successful
            .bearerToken(
                getBearerTokenWithAllRolesExcept(eroId = ERO_ID, excludedRoles = listOf("ero-vc-anonymous-admin"))
            )
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                buildBody(),
                CreateOfflineCommunicationConfirmationRequest::class.java
            )
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return forbidden given user with valid bearer token belonging to a different ero`() {
        wireMockService.stubCognitoJwtIssuerResponse()

        webTestClient.post()
            .uri(URI_TEMPLATE, ERO_ID, APPLICATION_ID)
            // the group ero-vc-anonymous-admin-$ERO_ID is required to be successful
            .bearerToken(getVCAnonymousAdminBearerToken(OTHER_ERO_ID))
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                buildBody(),
                CreateOfflineCommunicationConfirmationRequest::class.java
            )
            .exchange()
            .expectStatus()
            .isForbidden
    }

    private fun buildBody(
        gssCode: String = getRandomGssCode(),
        reason: OfflineCommunicationReason = OfflineCommunicationReason.APPLICATION_MINUS_REJECTED,
        channel: OfflineCommunicationChannel = OfflineCommunicationChannel.LETTER,
    ) =
        Mono.just(
            CreateOfflineCommunicationConfirmationRequest(
                gssCode = gssCode,
                reason = reason,
                channel = channel,
            )
        )
}
