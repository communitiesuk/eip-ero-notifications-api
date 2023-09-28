package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import java.util.UUID

internal class GetCommunicationStatisticsByApplicationIdIntegrationTest : IntegrationTest() {

    @Test
    fun `should return photo not requested and documents not requested for application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponse(
            photoRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return photo not requested and documents not requested for application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.APPLICATION_APPROVED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponse(
            photoRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return photo requested for application with photo resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.PHOTO_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponse(
            photoRequested = true,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for application with id document resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.ID_DOCUMENT_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponse(
            photoRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for application with id document required communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.ID_DOCUMENT_REQUIRED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponse(
            photoRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildUri(applicationId: String = UUID.randomUUID().toString()) =
        "/communications/statistics?applicationId=$applicationId"
}
