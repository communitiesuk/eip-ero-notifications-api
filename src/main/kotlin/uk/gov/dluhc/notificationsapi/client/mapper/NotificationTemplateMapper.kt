package uk.gov.dluhc.notificationsapi.client.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.NotifyTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
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
    private val notifyTemplateConfiguration: NotifyTemplateConfiguration, // TODO We might need to redesign the notifyTemplateConfiguration
    private val notificationTypeMapper: NotificationTypeMapper
) {

    fun fromNotificationTypeInLanguageForChannel(
        messageType: NotificationType,
        language: LanguageDto? = ENGLISH,
        channel: NotificationChannel // TODO Notification channel logic in next PR
    ): String {
        return if (language == null || language == ENGLISH) {
            when (messageType) {
                APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmailEnglish
                APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmailEnglish
                APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmailEnglish
                PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmailEnglish
            }
        } else {
            when (messageType) {
                APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmailWelsh
                APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmailWelsh
                APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmailWelsh
                PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmailWelsh
            }
        }
    }

    @Deprecated("Will be removed, please use fromNotificationTypeInLanguage()")
    fun fromNotificationType(messageType: NotificationType): String {
        return when (messageType) {
            APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmailEnglish
            APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmailEnglish
            APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmailEnglish
            PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmailEnglish
        }
    }

    fun fromTemplateTypeForChannelAndLanguage(
        templateType: TemplateType,
        language: LanguageDto? = ENGLISH,
        channel: NotificationChannel
    ): String =
        fromNotificationTypeInLanguageForChannel(
            messageType = notificationTypeMapper.toNotificationType(templateType),
            language = language,
            channel = channel
        )
}
