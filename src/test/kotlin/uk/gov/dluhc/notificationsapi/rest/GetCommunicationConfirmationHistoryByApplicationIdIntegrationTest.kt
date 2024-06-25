package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason
import uk.gov.dluhc.notificationsapi.models.CommunicationConfirmationHistoryResponse
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.model.buildElectoralRegistrationOfficeResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomCommunicationConfirmationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aCommunicationConfirmationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerTokenWithAllRolesExcept
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getVCAnonymousAdminBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCommunicationConfirmationHistoryEntryBuilder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
                getBearerTokenWithAllRolesExcept(eroId = ERO_ID, excludedRoles = listOf("ero-vc-anonymous-admin")),
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

    @Test
    fun `should successfully fetch the offline communication confirmation history for an anonymous application`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()
        val requestor = aRequestor()

        val sentConfirmation1 = aCommunicationConfirmationBuilder(
            id = aRandomCommunicationConfirmationId(),
            gssCode = eroResponse.localAuthorities[0].gssCode,
            sourceReference = applicationId,
            reason = CommunicationConfirmationReason.DOCUMENT_REJECTED,
            channel = CommunicationConfirmationChannel.LETTER,
            requestor = requestor,
            sentAt = LocalDateTime.of(2022, 10, 4, 13, 22, 18),
        )
        communicationConfirmationRepository.saveCommunicationConfirmation(sentConfirmation1)

        val sentConfirmation2 = aCommunicationConfirmationBuilder(
            id = aRandomCommunicationConfirmationId(),
            gssCode = eroResponse.localAuthorities[1].gssCode,
            sourceReference = applicationId,
            reason = CommunicationConfirmationReason.PHOTO_REJECTED,
            channel = CommunicationConfirmationChannel.EMAIL,
            requestor = requestor,
            sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24),
        )
        communicationConfirmationRepository.saveCommunicationConfirmation(sentConfirmation2)

        val expected = CommunicationConfirmationHistoryResponse(
            communicationConfirmations = listOf(
                aCommunicationConfirmationHistoryEntryBuilder(
                    id = sentConfirmation2.id!!,
                    gssCode = eroResponse.localAuthorities[1].gssCode,
                    reason = OfflineCommunicationReason.PHOTO_MINUS_REJECTED,
                    channel = OfflineCommunicationChannel.EMAIL,
                    requestor = requestor,
                    timestamp = OffsetDateTime.of(sentConfirmation2.sentAt, ZoneOffset.UTC),
                ),
                aCommunicationConfirmationHistoryEntryBuilder(
                    id = sentConfirmation1.id!!,
                    gssCode = eroResponse.localAuthorities[0].gssCode,
                    reason = OfflineCommunicationReason.DOCUMENT_MINUS_REJECTED,
                    channel = OfflineCommunicationChannel.LETTER,
                    requestor = requestor,
                    timestamp = OffsetDateTime.of(sentConfirmation1.sentAt, ZoneOffset.UTC),
                ),
            ),
        )

        // When
        val response = webTestClient.get()
            .uri(URI_TEMPLATE, ERO_ID, applicationId)
            // the group ero-vc-anonymous-admin-$ERO_ID is required to be successful
            .bearerToken(getVCAnonymousAdminBearerToken(ERO_ID, requestor))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationConfirmationHistoryResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }
}
