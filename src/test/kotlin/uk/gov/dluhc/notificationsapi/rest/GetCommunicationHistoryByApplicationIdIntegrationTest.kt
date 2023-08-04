package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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

    @ParameterizedTest
    @CsvSource(
        value = [
            ",ero-postal-admin",
            ",ero-proxy-admin",
            ",ero-oe-admin",
            "voter-card,ero-postal-admin",
            "voter-card,ero-proxy-admin",
            "voter-card,ero-oe-admin",
            "postal,ero-vc-admin",
            "postal,ero-proxy-admin",
            "postal,ero-oe-admin",
            "proxy,ero-vc-admin",
            "proxy,ero-postal-admin",
            "proxy,ero-oe-admin",
            "overseas,ero-vc-admin",
            "overseas,ero-postal-admin",
            "overseas,ero-proxy-admin"
        ]
    )
    fun `should return forbidden given user with valid bearer token belonging to a different admin group than the application type specified`(
        requestedSourceType: String?,
        authGroupPrefix: String
    ) {
        wireMockService.stubCognitoJwtIssuerResponse()

        webTestClient.get()
            .uri(buildUri(eroId = ERO_ID, sourceType = requestedSourceType))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "$authGroupPrefix-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun `should return bad request given un recognised source type parameter`() {
        wireMockService.stubCognitoJwtIssuerResponse()
        val requestEroId = ERO_ID
        val userGroupEroId = OTHER_ERO_ID

        webTestClient.get()
            .uri(buildUri(eroId = requestEroId, sourceType = "unknown"))
            .bearerToken(getBearerToken(eroId = userGroupEroId, groups = listOf("ero-$userGroupEroId", "ero-vc-admin-$userGroupEroId")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return response with no Communication Summaries for application with no previously sent Notifications`() {
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

    @ParameterizedTest
    @CsvSource(
        value = [
            ",VOTER_CARD,ero-vc-admin",
            "voter-card,VOTER_CARD,ero-vc-admin",
            "postal,POSTAL,ero-postal-admin",
            "proxy,PROXY,ero-proxy-admin",
            "overseas,OVERSEAS,ero-oe-admin"
        ]
    )
    fun `should return Communication Summaries for application`(
        requestedSourceType: String?,
        sourceType: SourceType,
        authGroupPrefix: String
    ) {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()
        val requestor = aRequestor()

        val sentNotification1 = aNotificationBuilder(
            id = aRandomNotificationId(),
            sourceReference = applicationId,
            sourceType = sourceType,
            gssCode = eroResponse.localAuthorities[0].gssCode,
            requestor = requestor,
            channel = Channel.EMAIL,
            type = NotificationType.PHOTO_RESUBMISSION,
            sentAt = LocalDateTime.of(2022, 10, 4, 13, 22, 18),
        )
        notificationRepository.saveNotification(
            sentNotification1
        )

        val sentNotification2 = aNotificationBuilder(
            id = aRandomNotificationId(),
            sourceReference = applicationId,
            sourceType = sourceType,
            gssCode = eroResponse.localAuthorities[0].gssCode,
            requestor = requestor,
            channel = Channel.EMAIL,
            type = NotificationType.APPLICATION_APPROVED,
            sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24),
        )
        notificationRepository.saveNotification(
            sentNotification2
        )

        val expected = CommunicationsHistoryResponse(
            communications = listOf(
                aCommunicationsSummaryBuilder(
                    id = sentNotification2.id!!,
                    requestor = aRequestor(),
                    channel = NotificationChannel.EMAIL,
                    templateType = TemplateType.APPLICATION_MINUS_APPROVED,
                    timestamp = OffsetDateTime.of(sentNotification2.sentAt, ZoneOffset.UTC),
                ),
                aCommunicationsSummaryBuilder(
                    id = sentNotification1.id!!,
                    requestor = aRequestor(),
                    channel = NotificationChannel.EMAIL,
                    templateType = TemplateType.PHOTO_MINUS_RESUBMISSION,
                    timestamp = OffsetDateTime.of(sentNotification1.sentAt, ZoneOffset.UTC),
                )
            )
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, eroId = ERO_ID, sourceType = requestedSourceType))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "$authGroupPrefix-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsHistoryResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildUri(eroId: String = ERO_ID, applicationId: String = UUID.randomUUID().toString(), sourceType: String? = null) =
        "/eros/$eroId/communications/applications/$applicationId" + ("?sourceType=$sourceType".takeIf { sourceType != null } ?: "")
}
