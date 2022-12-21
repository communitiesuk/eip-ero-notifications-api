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
    private val notifyTemplateConfiguration: NotifyTemplateConfiguration,
    private val notificationTypeMapper: NotificationTypeMapper
) {

    fun fromNotificationTypeForChannelInLanguage(
        notificationType: NotificationType,
        channel: NotificationChannel,
        language: LanguageDto?
    ): String {
        return when (channel) {
            NotificationChannel.EMAIL -> fromEmailNotificationTypeInLanguage(notificationType, language)
        }
    }

    fun fromTemplateTypeForChannelAndLanguage(
        templateType: TemplateType,
        channel: NotificationChannel,
        language: LanguageDto?
    ): String =
        fromNotificationTypeForChannelInLanguage(
            notificationType = notificationTypeMapper.toNotificationType(templateType),
            channel = channel,
            language = language
        )

    private fun fromEmailNotificationTypeInLanguage(
        notificationType: NotificationType,
        language: LanguageDto?
    ): String {
        return if (language == null || language == ENGLISH) {
            when (notificationType) {
                APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmailEnglish
                APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmailEnglish
                APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmailEnglish
                PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmailEnglish
            }
        } else {
            when (notificationType) {
                APPLICATION_RECEIVED -> notifyTemplateConfiguration.receivedEmailWelsh
                APPLICATION_APPROVED -> notifyTemplateConfiguration.approvedEmailWelsh
                APPLICATION_REJECTED -> notifyTemplateConfiguration.rejectedEmailWelsh
                PHOTO_RESUBMISSION -> notifyTemplateConfiguration.photoResubmissionEmailWelsh
            }
        }
    }
}
