package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotificationSummaryDto

@ExtendWith(MockitoExtension::class)
internal class StatisticsServiceTest {

    private lateinit var statisticsService: StatisticsService

    @Mock
    private lateinit var sentNotificationsService: SentNotificationsService

    companion object {
        private val testSourceReference = aSourceReference()
        private val notificationResponse = listOf(
            buildNotificationSummaryDto(
                type = NotificationType.REJECTED_SIGNATURE,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.REQUESTED_SIGNATURE,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.ID_DOCUMENT_REQUIRED,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.ID_DOCUMENT_RESUBMISSION,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.NINO_NOT_MATCHED,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.BESPOKE_COMM,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.NOT_REGISTERED_TO_VOTE,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.PHOTO_RESUBMISSION,
            ),
        )
    }

    @BeforeEach
    fun setUp() {
        statisticsService = StatisticsService(sentNotificationsService)
    }

    @Test
    fun `should only count relevant statistics for postal`() {
        // Given
        given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.POSTAL)).willReturn(notificationResponse)
        val expected = CommunicationsStatisticsResponse(
            numNotRegisteredToVoteCommsSent = 1,
            numSignatureRequestCommsSent = 3,
            numPhotoRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 3,
            bespokeCommunicationsSent = 1,
        )

        // When
        val actual = statisticsService.getApplicationStatistics(SourceType.POSTAL, testSourceReference)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should only count relevant statistics for proxy`() {
        // Given
        given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.PROXY)).willReturn(notificationResponse)
        val expected = CommunicationsStatisticsResponse(
            numNotRegisteredToVoteCommsSent = 1,
            numSignatureRequestCommsSent = 3,
            numPhotoRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 3,
            bespokeCommunicationsSent = 1,
        )

        // When
        val actual = statisticsService.getApplicationStatistics(SourceType.PROXY, testSourceReference)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should only count relevant statistics for vac`() {
        // Given
        given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.VOTER_CARD)).willReturn(notificationResponse)
        val expected = CommunicationsStatisticsResponse(
            numNotRegisteredToVoteCommsSent = 1,
            numSignatureRequestCommsSent = 0,
            numPhotoRequestCommsSent = 1,
            bespokeCommunicationsSent = 1,
            numIdentityDocumentRequestCommsSent = 3,
        )

        // When
        val actual = statisticsService.getApplicationStatistics(SourceType.VOTER_CARD, testSourceReference)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should only count relevant statistics for overseas`() {
        // Given
        given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.OVERSEAS)).willReturn(notificationResponse)
        val expected = CommunicationsStatisticsResponse(
            numNotRegisteredToVoteCommsSent = 1,
            numSignatureRequestCommsSent = 0,
            numPhotoRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 3,
            bespokeCommunicationsSent = 1,
        )

        // When
        val actual = statisticsService.getApplicationStatistics(SourceType.OVERSEAS, testSourceReference)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should only count relevant notifications for signatureRequested`() {
        // Given
        val expectedNotifications = listOf(
            NotificationType.REJECTED_SIGNATURE,
            NotificationType.REQUESTED_SIGNATURE,
            NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
        )

        // When
        val returnedNotifications = statisticsService.getNotificationsForStatistic("signatureRequested", notificationResponse)

        // Then
        returnedNotifications.forEach { notification ->
            assertThat(notification.type).isIn(expectedNotifications)
        }
    }

    @Test
    fun `should only count relevant notifications for identityDocumentsRequested`() {
        // Given
        val expectedNotifications = listOf(
            NotificationType.ID_DOCUMENT_REQUIRED,
            NotificationType.ID_DOCUMENT_RESUBMISSION,
            NotificationType.NINO_NOT_MATCHED,
        )

        // When
        val returnedNotifications = statisticsService.getNotificationsForStatistic("identityDocumentsRequested", notificationResponse)

        // Then
        returnedNotifications.forEach { notification ->
            assertThat(notification.type).isIn(expectedNotifications)
        }
    }

    @Test
    fun `should only count relevant notifications for bespokeCommunicationsSent`() {
        // Given
        val expectedNotifications = listOf(
            NotificationType.BESPOKE_COMM,
        )

        // When
        val returnedNotifications = statisticsService.getNotificationsForStatistic("bespokeCommunicationsSent", notificationResponse)

        // Then
        returnedNotifications.forEach { notification ->
            assertThat(notification.type).isIn(expectedNotifications)
        }
    }

    @Test
    fun `should only count relevant notifications for notRegisteredToVoteCommunication`() {
        // Given
        val expectedNotifications = listOf(
            NotificationType.NOT_REGISTERED_TO_VOTE,
        )

        // When
        val returnedNotifications = statisticsService.getNotificationsForStatistic("notRegisteredToVoteCommunication", notificationResponse)

        // Then
        returnedNotifications.forEach { notification ->
            assertThat(notification.type).isIn(expectedNotifications)
        }
    }

    @Test
    fun `should only count relevant notifications for photoRequested`() {
        // Given
        val expectedNotifications = listOf(
            NotificationType.PHOTO_RESUBMISSION,
        )

        // When
        val returnedNotifications = statisticsService.getNotificationsForStatistic("photoRequested", notificationResponse)

        // Then
        returnedNotifications.forEach { notification ->
            assertThat(notification.type).isIn(expectedNotifications)
        }
    }
}
