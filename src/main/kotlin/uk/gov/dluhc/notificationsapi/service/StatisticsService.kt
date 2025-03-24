package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse

@Service
class StatisticsService(private val sentNotificationsService: SentNotificationsService) {
    val statisticsPerService = mapOf(
        Pair(SourceType.POSTAL, listOf("signatureRequested", "identityDocumentsRequested", "bespokeCommunicationsSent", "notRegisteredToVoteCommunication")),
        Pair(SourceType.PROXY, listOf("signatureRequested", "identityDocumentsRequested", "bespokeCommunicationsSent", "notRegisteredToVoteCommunication")),
        Pair(SourceType.OVERSEAS, listOf("bespokeCommunicationsSent", "notRegisteredToVoteCommunication", "identityDocumentsRequested")),
        Pair(SourceType.VOTER_CARD, listOf("identityDocumentsRequested", "bespokeCommunicationsSent", "notRegisteredToVoteCommunication", "photoRequested")),
    )

    val notificationsPerStatistic = mapOf(
        Pair("signatureRequested", listOf(NotificationType.REJECTED_SIGNATURE, NotificationType.REQUESTED_SIGNATURE, NotificationType.REJECTED_SIGNATURE_WITH_REASONS)),
        Pair("identityDocumentsRequested", listOf(NotificationType.ID_DOCUMENT_REQUIRED, NotificationType.ID_DOCUMENT_RESUBMISSION, NotificationType.NINO_NOT_MATCHED)),
        Pair("bespokeCommunicationsSent", listOf(NotificationType.BESPOKE_COMM)),
        Pair("notRegisteredToVoteCommunication", listOf(NotificationType.NOT_REGISTERED_TO_VOTE)),
        Pair("photoRequested", listOf(NotificationType.PHOTO_RESUBMISSION)),
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
            numSignatureRequestCommsSent = if (statisticsPerService[service]?.contains("signatureRequested") == true) getNotificationsForStatistic("signatureRequested", notifications).size else 0,
            numNotRegisteredToVoteCommsSent = if (statisticsPerService[service]?.contains("notRegisteredToVoteCommunication") == true) getNotificationsForStatistic("notRegisteredToVoteCommunication", notifications).size else 0,
            numPhotoRequestCommsSent = if (statisticsPerService[service]?.contains("photoRequested") == true) getNotificationsForStatistic("photoRequested", notifications).size else 0,
            numIdentityDocumentRequestCommsSent = if (statisticsPerService[service]?.contains("identityDocumentsRequested") == true) getNotificationsForStatistic("identityDocumentsRequested", notifications).size else 0,
            bespokeCommunicationsSent = if (statisticsPerService[service]?.contains("bespokeCommunicationsSent") == true) getNotificationsForStatistic("bespokeCommunicationsSent", notifications).size else 0,
        )
    }

    fun getNotificationsForStatistic(
        statistic: String,
        notifications: List<NotificationSummaryDto>,
    ) = notifications.filter { it.type in notificationsPerStatistic[statistic]!! }
}
