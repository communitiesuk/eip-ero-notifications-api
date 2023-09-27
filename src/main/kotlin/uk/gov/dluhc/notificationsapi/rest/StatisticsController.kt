package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.service.SentNotificationsService

@RestController
@CrossOrigin
@RequestMapping("/communications/statistics")
class StatisticsController(
    private val sentNotificationsService: SentNotificationsService,
) {

    @GetMapping
    fun getVacCommunicationsStatistics(
        @RequestParam applicationId: String,
    ): CommunicationsStatisticsResponse {
        val notifications = sentNotificationsService.getNotificationsForApplication(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD
        )

        val photoRequested = notifications.any { it.type == NotificationType.PHOTO_RESUBMISSION }
        val identityDocumentsRequested = notifications.any {
            it.type in listOf(
                NotificationType.ID_DOCUMENT_REQUIRED,
                NotificationType.ID_DOCUMENT_RESUBMISSION
            )
        }

        return CommunicationsStatisticsResponse(
            photoRequested = photoRequested,
            identityDocumentsRequested = identityDocumentsRequested,
        )
    }
}