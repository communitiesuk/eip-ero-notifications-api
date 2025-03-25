package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory.BESPOKE_COMMUNICATION_SENT
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory.IDENTITY_DOCUMENTS_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory.NOT_REGISTERED_TO_VOTE_COMMUNICATION
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory.PHOTO_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.NotificationCategory.SIGNATURE_REQUESTED
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.notificationCategories
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse

@Service
class StatisticsRetrievalService(private val sentNotificationsService: SentNotificationsService) {
    val statisticsPerService = mapOf(
        SourceType.POSTAL to listOf(SIGNATURE_REQUESTED, IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION),
        SourceType.PROXY to listOf(SIGNATURE_REQUESTED, IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION),
        SourceType.OVERSEAS to listOf(IDENTITY_DOCUMENTS_REQUESTED, BESPOKE_COMMUNICATION_SENT, NOT_REGISTERED_TO_VOTE_COMMUNICATION),
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
            bespokeCommunicationsSent = getNumberOfNotificationsForCategory(service, BESPOKE_COMMUNICATION_SENT, notifications),
        )
    }

    fun getNotificationsForStatistic(
        notificationCategory: NotificationCategory,
        notifications: List<NotificationSummaryDto>,
    ) = notifications.filter { it.type in notificationCategories[notificationCategory]!! }

    private fun getNumberOfNotificationsForCategory(service: SourceType, notificationCategory: NotificationCategory, notifications: List<NotificationSummaryDto>): Int =
        if (statisticsPerService[service]?.contains(notificationCategory) == true) getNotificationsForStatistic(notificationCategory, notifications).size else 0
}
