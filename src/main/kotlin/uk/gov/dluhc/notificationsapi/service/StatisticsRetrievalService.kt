package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory.BESPOKE_COMMUNICATION_SENT
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory.IDENTITY_DOCUMENTS_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory.NOT_REGISTERED_TO_VOTE_COMMUNICATION
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory.PHOTO_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.StatisticsNotificationCategory.SIGNATURE_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.statisticsNotificationCategories
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse

@Service
class StatisticsRetrievalService(private val sentNotificationsService: SentNotificationsService) {
    val statisticsPerService = mapOf(
        SourceType.POSTAL to listOf(SIGNATURE_REQUESTED, IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION),
        SourceType.PROXY to listOf(SIGNATURE_REQUESTED, IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION),
        SourceType.OVERSEAS to listOf(IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT),
        SourceType.VOTER_CARD to listOf(IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION, PHOTO_REQUESTED),
    )

    fun getApplicationStatistics(
        service: SourceType,
        applicationId: String,
    ): CommunicationsStatisticsResponse {
        val notifications = sentNotificationsService.getNotificationsForApplication(
            sourceReference = applicationId,
            sourceType = service,
        )
        return CommunicationsStatisticsResponse(
            numSignatureRequestCommsSent = getNumberOfNotificationsForCategory(service, SIGNATURE_REQUESTED, notifications),
            numNotRegisteredToVoteCommsSent = getNumberOfNotificationsForCategory(service, NOT_REGISTERED_TO_VOTE_COMMUNICATION, notifications),
            numPhotoRequestCommsSent = getNumberOfNotificationsForCategory(service, PHOTO_REQUESTED, notifications),
            numIdentityDocumentRequestCommsSent = getNumberOfNotificationsForCategory(service, IDENTITY_DOCUMENTS_REQUESTED, notifications),
            numBespokeCommunicationsSent = getNumberOfNotificationsForCategory(service, BESPOKE_COMMUNICATION_SENT, notifications),
        )
    }

    fun getNotificationsForStatistic(
        statisticsNotificationCategory: StatisticsNotificationCategory,
        notifications: List<NotificationSummaryDto>,
    ) = notifications.filter { it.type in statisticsNotificationCategories[statisticsNotificationCategory]!! }

    private fun getNumberOfNotificationsForCategory(service: SourceType, statisticsNotificationCategory: StatisticsNotificationCategory, notifications: List<NotificationSummaryDto>): Int =
        if (statisticsPerService[service]?.contains(statisticsNotificationCategory) == true) getNotificationsForStatistic(statisticsNotificationCategory, notifications).size else 0
}
