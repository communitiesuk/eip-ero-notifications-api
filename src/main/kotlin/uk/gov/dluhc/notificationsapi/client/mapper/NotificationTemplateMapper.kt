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
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.NotificationTemplateNotFoundException

/**
 * Gets the Notification Template ID configured for each message type.
 */
@Component
class NotificationTemplateMapper(
    private val notifyEmailTemplateConfiguration: NotifyEmailTemplateConfiguration,
    private val notifyLetterTemplateConfiguration: NotifyLetterTemplateConfiguration,
) {
    fun fromNotificationTypeForChannelInLanguage(
        sourceType: SourceType,
        notificationType: NotificationType,
        channel: NotificationChannel,
        language: LanguageDto?
    ): String {
        return when (channel) {
            NotificationChannel.EMAIL -> fromEmailNotificationTypeInLanguage(sourceType, notificationType, language)
            NotificationChannel.LETTER -> fromLetterNotificationTypeInLanguage(notificationType, language)
        }
    }

    private fun fromEmailNotificationTypeInLanguage(sourceType: SourceType, notificationType: NotificationType, language: LanguageDto?) =
        if (useEnglishTemplate(language)) englishEmail(sourceType, notificationType) else welshEmail(sourceType, notificationType)

    private fun useEnglishTemplate(language: LanguageDto?) = language == null || language == ENGLISH

    private fun welshEmail(sourceType: SourceType, notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> getApplicationReceivedWelshEmailTemplateConfig(sourceType)
        APPLICATION_APPROVED -> notifyEmailTemplateConfiguration.approvedWelsh
        PHOTO_RESUBMISSION -> notifyEmailTemplateConfiguration.photoResubmissionWelsh
        ID_DOCUMENT_RESUBMISSION -> notifyEmailTemplateConfiguration.idDocumentResubmissionWelsh
        else -> {
            throw IllegalStateException("No email template defined in Welsh for notification type $notificationType and sourceType $sourceType")
        }
    }

    private fun englishEmail(sourceType: SourceType, notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> getApplicationReceivedEnglishEmailTemplateConfig(sourceType)
        APPLICATION_APPROVED -> notifyEmailTemplateConfiguration.approvedEnglish
        PHOTO_RESUBMISSION -> notifyEmailTemplateConfiguration.photoResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION -> notifyEmailTemplateConfiguration.idDocumentResubmissionEnglish
        else -> {
            throw IllegalStateException("No email template defined in English for notification type $notificationType and sourceType $sourceType")
        }
    }

    private fun fromLetterNotificationTypeInLanguage(notificationType: NotificationType, language: LanguageDto?) =
        if (useEnglishTemplate(language)) englishLetter(notificationType) else welshLetter(notificationType)

    private fun welshLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedWelsh
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedWelsh
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionWelsh
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionWelsh
        else -> {
            throw IllegalStateException("No letter template defined in Welsh for notification type $notificationType")
        }
    }

    private fun englishLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedEnglish
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedEnglish
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionEnglish
        else -> {
            throw IllegalStateException("No letter template defined in English for notification type $notificationType")
        }
    }

    private fun getApplicationReceivedEnglishEmailTemplateConfig(sourceType: SourceType) = when (sourceType) {
        SourceType.VOTER_CARD -> notifyEmailTemplateConfiguration.receivedEnglish
        SourceType.POSTAL -> notifyEmailTemplateConfiguration.postalReceivedEnglish
        SourceType.PROXY -> notifyEmailTemplateConfiguration.proxyReceivedEnglish
        else -> {
            throw NotificationTemplateNotFoundException("No email template defined in English for source type $sourceType")
        }
    }

    private fun getApplicationReceivedWelshEmailTemplateConfig(sourceType: SourceType) = when (sourceType) {
        SourceType.VOTER_CARD -> notifyEmailTemplateConfiguration.receivedWelsh
        SourceType.POSTAL -> notifyEmailTemplateConfiguration.postalReceivedWelsh
        SourceType.PROXY -> notifyEmailTemplateConfiguration.proxyReceivedWelsh
        else -> {
            throw NotificationTemplateNotFoundException("No email template defined in Welsh for source type $sourceType")
        }
    }
}
