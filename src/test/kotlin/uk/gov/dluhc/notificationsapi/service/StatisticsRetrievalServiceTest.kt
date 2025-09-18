package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotificationSummaryDto

@ExtendWith(MockitoExtension::class)
internal class StatisticsRetrievalServiceTest {

    private lateinit var statisticsRetrievalService: StatisticsRetrievalService

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
            buildNotificationSummaryDto(
                type = NotificationType.SIGNATURE_RESUBMISSION,
            ),
            buildNotificationSummaryDto(
                type = NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS,
            ),
        )
    }

    @BeforeEach
    fun setUp() {
        statisticsRetrievalService = StatisticsRetrievalService(sentNotificationsService)
    }

    @Nested
    inner class GetApplicationStatistics {
        @Test
        fun `should only count relevant statistics for postal`() {
            // Given
            given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.POSTAL)).willReturn(notificationResponse)
            val expected = CommunicationsStatisticsResponse(
                numNotRegisteredToVoteCommsSent = 1,
                numSignatureRequestCommsSent = 5,
                numPhotoRequestCommsSent = 0,
                numIdentityDocumentRequestCommsSent = 3,
                numBespokeCommunicationsSent = 1,
            )

            // When
            val actual = statisticsRetrievalService.getApplicationStatistics(SourceType.POSTAL, testSourceReference)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun `should only count relevant statistics for proxy`() {
            // Given
            given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.PROXY)).willReturn(notificationResponse)
            val expected = CommunicationsStatisticsResponse(
                numNotRegisteredToVoteCommsSent = 1,
                numSignatureRequestCommsSent = 5,
                numPhotoRequestCommsSent = 0,
                numIdentityDocumentRequestCommsSent = 3,
                numBespokeCommunicationsSent = 1,
            )

            // When
            val actual = statisticsRetrievalService.getApplicationStatistics(SourceType.PROXY, testSourceReference)

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
                numBespokeCommunicationsSent = 1,
                numIdentityDocumentRequestCommsSent = 3,
            )

            // When
            val actual = statisticsRetrievalService.getApplicationStatistics(SourceType.VOTER_CARD, testSourceReference)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun `should only count relevant statistics for overseas`() {
            // Given
            given(sentNotificationsService.getNotificationsForApplication(testSourceReference, SourceType.OVERSEAS)).willReturn(notificationResponse)
            val expected = CommunicationsStatisticsResponse(
                numNotRegisteredToVoteCommsSent = 0,
                numSignatureRequestCommsSent = 0,
                numPhotoRequestCommsSent = 0,
                numIdentityDocumentRequestCommsSent = 3,
                numBespokeCommunicationsSent = 1,
            )

            // When
            val actual = statisticsRetrievalService.getApplicationStatistics(SourceType.OVERSEAS, testSourceReference)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class GetNotificationsForStatistic {

        @Test
        fun `should only count relevant notifications for SIGNATURE_REQUESTED`() {
            // Given
            val expectedNotifications = listOf(
                NotificationType.REJECTED_SIGNATURE,
                NotificationType.REQUESTED_SIGNATURE,
                NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
                NotificationType.SIGNATURE_RESUBMISSION,
                NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS,
            )

            // When
            val returnedNotifications = statisticsRetrievalService.getNotificationsForStatistic(StatisticsNotificationCategory.SIGNATURE_REQUESTED, notificationResponse)

            // Then
            returnedNotifications.forEach { notification ->
                assertThat(notification.type).isIn(expectedNotifications)
            }
        }

        @Test
        fun `should only count relevant notifications for IDENTITY_DOCUMENTS_REQUESTED`() {
            // Given
            val expectedNotifications = listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION,
                NotificationType.NINO_NOT_MATCHED,
            )

            // When
            val returnedNotifications = statisticsRetrievalService.getNotificationsForStatistic(StatisticsNotificationCategory.IDENTITY_DOCUMENTS_REQUESTED, notificationResponse)

            // Then
            returnedNotifications.forEach { notification ->
                assertThat(notification.type).isIn(expectedNotifications)
            }
        }

        @Test
        fun `should only count relevant notifications for BESPOKE_COMMUNICATION_SENT`() {
            // Given
            val expectedNotifications = listOf(
                NotificationType.BESPOKE_COMM,
            )

            // When
            val returnedNotifications = statisticsRetrievalService.getNotificationsForStatistic(StatisticsNotificationCategory.BESPOKE_COMMUNICATION_SENT, notificationResponse)

            // Then
            returnedNotifications.forEach { notification ->
                assertThat(notification.type).isIn(expectedNotifications)
            }
        }

        @Test
        fun `should only count relevant notifications for NOT_REGISTERED_TO_VOTE_COMMUNICATION`() {
            // Given
            val expectedNotifications = listOf(
                NotificationType.NOT_REGISTERED_TO_VOTE,
            )

            // When
            val returnedNotifications = statisticsRetrievalService.getNotificationsForStatistic(StatisticsNotificationCategory.NOT_REGISTERED_TO_VOTE_COMMUNICATION, notificationResponse)

            // Then
            returnedNotifications.forEach { notification ->
                assertThat(notification.type).isIn(expectedNotifications)
            }
        }

        @Test
        fun `should only count relevant notifications for PHOTO_REQUESTED`() {
            // Given
            val expectedNotifications = listOf(
                NotificationType.PHOTO_RESUBMISSION,
            )

            // When
            val returnedNotifications = statisticsRetrievalService.getNotificationsForStatistic(StatisticsNotificationCategory.PHOTO_REQUESTED, notificationResponse)

            // Then
            returnedNotifications.forEach { notification ->
                assertThat(notification.type).isIn(expectedNotifications)
            }
        }
    }
}
