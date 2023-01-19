package uk.gov.dluhc.notificationsapi.client.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_APPROVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_RECEIVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_REJECTED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION

/**
 * Gets the Notification Template ID configured for each message type.
 */
@Component
class NotificationTemplateMapper(
    private val notifyEmailTemplateConfiguration: NotifyEmailTemplateConfiguration,
    private val notifyLetterTemplateConfiguration: NotifyLetterTemplateConfiguration,
) {

    fun fromTemplateTypeForChannelAndLanguage(
        notificationType: NotificationType,
        channel: NotificationChannel,
        language: LanguageDto?
    ): String =
        fromNotificationTypeForChannelInLanguage(
            notificationType = notificationType,
            channel = channel,
            language = language
        )

    fun fromNotificationTypeForChannelInLanguage(
        notificationType: NotificationType,
        channel: NotificationChannel,
        language: LanguageDto?
    ): String {
        return when (channel) {
            NotificationChannel.EMAIL -> fromEmailNotificationTypeInLanguage(notificationType, language)
            NotificationChannel.LETTER -> fromLetterNotificationTypeInLanguage(notificationType, language)
        }
    }

    private fun fromEmailNotificationTypeInLanguage(notificationType: NotificationType, language: LanguageDto?) =
        if (useEnglishTemplate(language)) englishEmail(notificationType) else welshEmail(notificationType)

    private fun useEnglishTemplate(language: LanguageDto?) = language == null || language == ENGLISH

    private fun welshEmail(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyEmailTemplateConfiguration.receivedWelsh
        APPLICATION_APPROVED -> notifyEmailTemplateConfiguration.approvedWelsh
        APPLICATION_REJECTED -> notifyEmailTemplateConfiguration.rejectedWelsh
        PHOTO_RESUBMISSION -> notifyEmailTemplateConfiguration.photoResubmissionWelsh
        ID_DOCUMENT_RESUBMISSION -> notifyEmailTemplateConfiguration.idDocumentResubmissionWelsh
    }

    private fun englishEmail(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyEmailTemplateConfiguration.receivedEnglish
        APPLICATION_APPROVED -> notifyEmailTemplateConfiguration.approvedEnglish
        APPLICATION_REJECTED -> notifyEmailTemplateConfiguration.rejectedEnglish
        PHOTO_RESUBMISSION -> notifyEmailTemplateConfiguration.photoResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION -> notifyEmailTemplateConfiguration.idDocumentResubmissionEnglish
    }

    private fun fromLetterNotificationTypeInLanguage(notificationType: NotificationType, language: LanguageDto?) =
        if (useEnglishTemplate(language)) englishLetter(notificationType) else welshLetter(notificationType)

    private fun welshLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedWelsh
        APPLICATION_APPROVED -> notifyLetterTemplateConfiguration.approvedWelsh
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedWelsh
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionWelsh
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionWelsh
    }

    private fun englishLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedEnglish
        APPLICATION_APPROVED -> notifyLetterTemplateConfiguration.approvedEnglish
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedEnglish
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionEnglish
    }
}
