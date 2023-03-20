package uk.gov.dluhc.notificationsapi.client.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.AbstractNotifyEmailTemplateConfiguration
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
import uk.gov.dluhc.notificationsapi.dto.SourceType.OVERSEAS
import uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.dto.SourceType.PROXY
import uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD
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

    private fun fromEmailNotificationTypeInLanguage(
        sourceType: SourceType,
        notificationType: NotificationType,
        language: LanguageDto?
    ): String {
        val config = getSourceTemplateEmailTemplateConfiguration(sourceType)
        return if (useEnglishTemplate(language)) englishEmail(config, notificationType)
        else welshEmail(config, notificationType)
    }

    private fun getSourceTemplateEmailTemplateConfiguration(sourceType: SourceType): AbstractNotifyEmailTemplateConfiguration {
        val config = when (sourceType) {
            OVERSEAS -> notifyEmailTemplateConfiguration.overseas
            POSTAL -> notifyEmailTemplateConfiguration.postal
            PROXY -> notifyEmailTemplateConfiguration.proxy
            VOTER_CARD -> notifyEmailTemplateConfiguration.voterCard
            else -> {
                throw NotificationTemplateNotFoundException("No email template configuration defined for sourceType $sourceType")
            }
        }
        return config
    }

    private fun useEnglishTemplate(language: LanguageDto?) = language == null || language == ENGLISH

    private fun welshEmail(config: AbstractNotifyEmailTemplateConfiguration, notificationType: NotificationType) =
        when (notificationType) {
            APPLICATION_RECEIVED -> config.receivedWelsh
            APPLICATION_APPROVED -> config.approvedWelsh
            PHOTO_RESUBMISSION -> config.photoResubmissionWelsh
            ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionWelsh
            else -> {
                throw NotificationTemplateNotFoundException("No email template defined in Welsh for notification type $notificationType and sourceType ${config.sourceType}")
            }
        }
            ?: throw NotificationTemplateNotFoundException("No email template defined in Welsh for notification type $notificationType and sourceType ${config.sourceType}")

    private fun englishEmail(config: AbstractNotifyEmailTemplateConfiguration, notificationType: NotificationType) =
        when (notificationType) {
            APPLICATION_RECEIVED -> config.receivedEnglish
            APPLICATION_APPROVED -> config.approvedEnglish
            PHOTO_RESUBMISSION -> config.photoResubmissionEnglish
            ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionEnglish
            else -> {
                throw NotificationTemplateNotFoundException("No email template defined in English for notification type $notificationType and sourceType ${config.sourceType}")
            }
        }
            ?: throw NotificationTemplateNotFoundException("No email template defined in English for notification type $notificationType and sourceType ${config.sourceType}")

    private fun fromLetterNotificationTypeInLanguage(
        notificationType: NotificationType,
        language: LanguageDto?
    ) = (if (useEnglishTemplate(language)) englishLetter(notificationType) else welshLetter(notificationType))
        ?: throw NotificationTemplateNotFoundException("No letter template defined in $language for notification type $notificationType")

    private fun welshLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedWelsh
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedWelsh
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionWelsh
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionWelsh
        else -> {
            throw NotificationTemplateNotFoundException("No letter template defined in Welsh for notification type $notificationType")
        }
    }

    private fun englishLetter(notificationType: NotificationType) = when (notificationType) {
        APPLICATION_RECEIVED -> notifyLetterTemplateConfiguration.receivedEnglish
        APPLICATION_REJECTED -> notifyLetterTemplateConfiguration.rejectedEnglish
        PHOTO_RESUBMISSION -> notifyLetterTemplateConfiguration.photoResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION -> notifyLetterTemplateConfiguration.idDocumentResubmissionEnglish
        else -> {
            throw NotificationTemplateNotFoundException("No letter template defined in English for notification type $notificationType")
        }
    }
}
