package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository

/**
 * Class exposing service methods relating to Notifications sent for applications.
 */
@Service
class SentNotificationsService(
    private val eroService: EroService,
    private val notificationRepository: NotificationRepository,
) {

    fun getNotificationHistoryForApplication(sourceReference: String, eroId: String) {
        eroService.lookupGssCodesForEro(eroId).let { gssCodes ->
            // notificationRepository.getBySourceReference()
        }
    }
}
