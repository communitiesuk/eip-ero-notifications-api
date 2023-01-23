package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.springframework.http.MediaType
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsHistoryResponse
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.getDifferentRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.getRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.model.buildElectoralRegistrationOfficeResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.aCommunicationsSummaryBuilder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
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

    @Test
    fun `should return response with no NotificationSummaries for application with no previously sent Notifications`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()

        val expected = CommunicationsHistoryResponse(
            communications = emptyList()
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, eroId = ERO_ID))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "ero-vc-admin-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsHistoryResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return NotificationSummaries for application`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()

        val notificationId = aRandomNotificationId()
        val requestor = aRequestor()
        val sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24)
        notificationRepository.saveNotification(
            aNotificationBuilder(
                id = notificationId,
                sourceReference = applicationId,
                sourceType = SourceType.VOTER_CARD,
                gssCode = eroResponse.localAuthorities[0].gssCode,
                requestor = requestor,
                channel = Channel.EMAIL,
                type = NotificationType.APPLICATION_APPROVED,
                sentAt = sentAt,
            )
        )

        val expected = CommunicationsHistoryResponse(
            communications = listOf(
                aCommunicationsSummaryBuilder(
                    id = notificationId,
                    requestor = aRequestor(),
                    channel = NotificationChannel.EMAIL,
                    templateType = TemplateType.APPLICATION_MINUS_APPROVED,
                    timestamp = OffsetDateTime.of(sentAt, ZoneOffset.UTC),
                )
            )
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, eroId = ERO_ID))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "ero-vc-admin-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsHistoryResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildUri(eroId: String = ERO_ID, applicationId: String = UUID.randomUUID().toString()) =
        "/eros/$eroId/communications/applications/$applicationId"
}
