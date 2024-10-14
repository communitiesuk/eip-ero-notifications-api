package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.InvalidSourceTypeException
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
        val identityDocumentsRequested = notifications.filter {
            it.type in listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION,
            )
        }.size

        val bespokeCommunications = notifications.filter { it.type == NotificationType.BESPOKE_COMM }.size

        val hasSentNotRegisteredToVoteCommunication = notifications.any { it.type == NotificationType.NOT_REGISTERED_TO_VOTE }

        return CommunicationsStatisticsResponseVAC(
            photoRequested = photoRequested,
            identityDocumentsRequested = identityDocumentsRequested > 0,
            bespokeCommunicationsSent = bespokeCommunications,
            hasSentNotRegisteredToVoteCommunication = hasSentNotRegisteredToVoteCommunication,
            numIdentityDocumentRequestCommsSent = identityDocumentsRequested,
        )
    }

    @GetMapping("/oava/{oavaService}")
    fun getOAVACommunicationsStatistics(
        @RequestParam applicationId: String,
        @PathVariable oavaService: String,
    ): CommunicationsStatisticsResponseOAVA {
        val notifications = sentNotificationsService.getNotificationsForApplication(
            sourceReference = applicationId,
            sourceType = when (oavaService) {
                "postal" -> SourceType.POSTAL
                "proxy" -> SourceType.PROXY
                "overseas" -> SourceType.OVERSEAS
                else -> throw InvalidSourceTypeException(oavaService)
            },
        )

        val signatureRequested = notifications.filter {
            it.type in listOf(
                NotificationType.REJECTED_SIGNATURE,
                NotificationType.REQUESTED_SIGNATURE,
                NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
            )
        }.size
        val identityDocumentsRequested = notifications.filter {
            it.type in listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION,
                NotificationType.NINO_NOT_MATCHED,
            )
        }.size

        val bespokeCommunications = notifications.filter { it.type == NotificationType.BESPOKE_COMM }.size

        val hasSentNotRegisteredToVoteCommunication = notifications.any { it.type == NotificationType.NOT_REGISTERED_TO_VOTE }

        return CommunicationsStatisticsResponseOAVA(
            signatureRequested = signatureRequested > 0,
            identityDocumentsRequested = identityDocumentsRequested > 0,
            bespokeCommunicationsSent = bespokeCommunications,
            hasSentNotRegisteredToVoteCommunication = hasSentNotRegisteredToVoteCommunication,
            numSignatureRequestCommsSent = signatureRequested,
            numIdentityDocumentRequestCommsSent = identityDocumentsRequested,
        )
    }
}
