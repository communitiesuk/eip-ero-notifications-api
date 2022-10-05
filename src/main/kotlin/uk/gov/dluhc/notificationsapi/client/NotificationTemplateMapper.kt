package uk.gov.dluhc.notificationsapi.client

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.NotifyTemplateConfiguration
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType

/**
 * Gets the Notification Template ID configured for each message type.
 */
@Component
class NotificationTemplateMapper(private val notifyTemplateConfiguration: NotifyTemplateConfiguration) {

    fun fromNotificationType(messageType: NotificationType): String {
        return when (messageType) {
            NotificationType.APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmail
            NotificationType.APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmail
            NotificationType.APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmail
        }
    }
}
