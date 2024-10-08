package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.models.SentCommunicationResponse
import uk.gov.dluhc.notificationsapi.testsupport.bearerToken
import uk.gov.dluhc.notificationsapi.testsupport.getDifferentRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.getRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.model.buildElectoralRegistrationOfficeResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.UNAUTHORIZED_BEARER_TOKEN
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getBearerToken
import java.time.LocalDateTime
import java.util.*
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApi

internal class GetCommunicationByIdIntegrationTest : IntegrationTest() {

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
    fun `should return subject and body of a sent communication when given valid application id and communication id`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()
        val notificationId = aRandomNotificationId()
        val sourceType = SourceTypeEntity.POSTAL
        val sourceTypeApiValue = SourceTypeApi.POSTAL.value
        val authGroupPrefix = "ero-postal-admin"
        val requestor = aRequestor()

        val sentNotification = aNotificationBuilder(
            id = notificationId,
            sourceReference = applicationId,
            sourceType = sourceType,
            gssCode = eroResponse.localAuthorities[0].gssCode,
            requestor = requestor,
            channel = Channel.EMAIL,
            type = NotificationType.PHOTO_RESUBMISSION,
            notifyDetails = aNotifyDetails(),
            sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24),
        )

        notificationRepository.saveNotification(sentNotification)

        val expected = SentCommunicationResponse(
            subject = sentNotification.notifyDetails!!.subject!!,
            body = sentNotification.notifyDetails!!.body!!,
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(eroId = ERO_ID, applicationId = applicationId, notificationId = notificationId.toString(), sourceType = sourceTypeApiValue))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "$authGroupPrefix-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(SentCommunicationResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return not found when given valid application id and communication id but the communication doesn't belong to that application`() {
        // Given
        wireMockService.stubCognitoJwtIssuerResponse()
        val eroResponse = buildElectoralRegistrationOfficeResponse(id = ERO_ID)
        wireMockService.stubEroManagementGetEroByEroId(eroResponse, ERO_ID)

        val applicationId = aRandomSourceReference()
        val anotherApplicationId = aRandomSourceReference()
        val notificationId = aRandomNotificationId()
        val sourceType = SourceTypeEntity.POSTAL
        val sourceTypeApiValue = SourceTypeApi.POSTAL.value
        val authGroupPrefix = "ero-postal-admin"
        val requestor = aRequestor()

        val sentNotification = aNotificationBuilder(
            id = notificationId,
            sourceReference = applicationId,
            sourceType = sourceType,
            gssCode = eroResponse.localAuthorities[0].gssCode,
            requestor = requestor,
            channel = Channel.EMAIL,
            type = NotificationType.PHOTO_RESUBMISSION,
            notifyDetails = aNotifyDetails(),
            sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24),
        )

        val anotherSentNotification = aNotificationBuilder(
            id = aRandomNotificationId(),
            sourceReference = anotherApplicationId,
            sourceType = sourceType,
            gssCode = eroResponse.localAuthorities[0].gssCode,
            requestor = requestor,
            channel = Channel.EMAIL,
            type = NotificationType.PHOTO_RESUBMISSION,
            notifyDetails = aNotifyDetails(),
            sentAt = LocalDateTime.of(2022, 10, 6, 9, 58, 24),
        )

        notificationRepository.saveNotification(sentNotification)
        notificationRepository.saveNotification(anotherSentNotification)

        // When, Then
        webTestClient.get()
            .uri(buildUri(eroId = ERO_ID, applicationId = anotherApplicationId, notificationId = notificationId.toString(), sourceType = sourceTypeApiValue))
            .bearerToken(getBearerToken(eroId = ERO_ID, groups = listOf("ero-$ERO_ID", "$authGroupPrefix-$ERO_ID")))
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    private fun buildUri(eroId: String = ERO_ID, applicationId: String = UUID.randomUUID().toString(), notificationId: String = UUID.randomUUID().toString(), sourceType: String = SourceTypeApi.POSTAL.value) =
        "/eros/$eroId/communications/applications/$applicationId/$notificationId?sourceType=$sourceType"
}
