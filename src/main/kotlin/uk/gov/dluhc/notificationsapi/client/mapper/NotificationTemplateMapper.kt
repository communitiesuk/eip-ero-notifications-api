package uk.gov.dluhc.notificationsapi.client.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.NotifyTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_APPROVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_RECEIVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_REJECTED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.models.TemplateType

/**
 * Gets the Notification Template ID configured for each message type.
 */
@Component
class NotificationTemplateMapper(
    private val notifyTemplateConfiguration: NotifyTemplateConfiguration,
    private val notificationTypeMapper: NotificationTypeMapper
) {

    fun fromNotificationType(messageType: NotificationType): String {
        return when (messageType) {
            APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmail
            APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmail
            APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmail
            PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmail
        }
    }

    fun fromTemplateType(templateType: TemplateType): String =
        fromNotificationType(notificationTypeMapper.toNotificationType(templateType))
}
