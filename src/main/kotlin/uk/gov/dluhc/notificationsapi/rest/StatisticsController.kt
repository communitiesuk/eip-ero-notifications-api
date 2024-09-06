package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponseOAVA
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponseVAC
import uk.gov.dluhc.notificationsapi.service.SentNotificationsService

@RestController
@CrossOrigin
@RequestMapping("/communications/statistics")
class StatisticsController(
    private val sentNotificationsService: SentNotificationsService,
) {
    @GetMapping("/vac")
    fun getVacCommunicationsStatistics(
        @RequestParam applicationId: String,
    ): CommunicationsStatisticsResponseVAC {
        val notifications = sentNotificationsService.getNotificationsForApplication(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
        )

        val photoRequested = notifications.any { it.type == NotificationType.PHOTO_RESUBMISSION }
        val identityDocumentsRequested = notifications.any {
            it.type in listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION,
            )
        }

        val bespokeCommunications = notifications.filter { it.type == NotificationType.BESPOKE_COMM }.size

        val hasSentNotRegisteredToVoteCommunication = notifications.any { it.type == NotificationType.INVITE_TO_REGISTER }

        return CommunicationsStatisticsResponseVAC(
            photoRequested = photoRequested,
            identityDocumentsRequested = identityDocumentsRequested,
            bespokeCommunicationsSent = bespokeCommunications,
            hasSentNotRegisteredToVoteCommunication = hasSentNotRegisteredToVoteCommunication,
        )
    }

    @GetMapping("/oava/{oavaService}")
    fun getOAVACommunicationsStatistics(
        @RequestParam applicationId: String,
        @PathVariable oavaService: String,
    ): CommunicationsStatisticsResponseOAVA {
        val notifications = sentNotificationsService.getNotificationsForApplication(
            sourceReference = applicationId,
            sourceType = if (oavaService == "postal") SourceType.POSTAL else SourceType.PROXY,
        )

        val signatureRequested = notifications.any {
            it.type in listOf(
                NotificationType.REJECTED_SIGNATURE,
                NotificationType.REQUESTED_SIGNATURE,
                NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
            )
        }
        val identityDocumentsRequested = notifications.any {
            it.type in listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION,
                NotificationType.NINO_NOT_MATCHED,
            )
        }

        val bespokeCommunications = notifications.filter { it.type == NotificationType.BESPOKE_COMM }.size

        val hasSentNotRegisteredToVoteCommunication = notifications.any { it.type == NotificationType.INVITE_TO_REGISTER }

        return CommunicationsStatisticsResponseOAVA(
            signatureRequested = signatureRequested,
            identityDocumentsRequested = identityDocumentsRequested,
            bespokeCommunicationsSent = bespokeCommunications,
            hasSentNotRegisteredToVoteCommunication = hasSentNotRegisteredToVoteCommunication,
        )
    }
}
