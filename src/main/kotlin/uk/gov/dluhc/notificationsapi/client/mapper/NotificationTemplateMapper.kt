package uk.gov.dluhc.notificationsapi.client.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.config.AbstractNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.AbstractNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.CommonTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_APPROVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_RECEIVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.APPLICATION_REJECTED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.BESPOKE_COMM
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_REQUIRED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.NINO_NOT_MATCHED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST
import uk.gov.dluhc.notificationsapi.dto.NotificationType.NOT_REGISTERED_TO_VOTE
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PARENT_GUARDIAN_PROOF_REQUIRED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_DOCUMENT
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_PARENT_GUARDIAN
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_PREVIOUS_ADDRESS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RECEIVED
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.SourceType.OVERSEAS
import uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.dto.SourceType.PROXY
import uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.exception.NotificationTemplateNotFoundException

@Component
class NotificationTemplateMapper(
    private val notifyEmailTemplateConfiguration: NotifyEmailTemplateConfiguration,
    private val notifyLetterTemplateConfiguration: NotifyLetterTemplateConfiguration,
) {
    fun fromNotificationTypeForChannelInLanguage(
        sourceType: SourceType,
        notificationType: NotificationType,
        channel: CommunicationChannel,
        language: LanguageDto?,
    ): String {
        return when (channel) {
            CommunicationChannel.EMAIL -> fromEmailNotificationTypeInLanguage(sourceType, notificationType, language)
            CommunicationChannel.LETTER -> fromLetterNotificationTypeInLanguage(sourceType, notificationType, language)
        }
    }

    fun fromNotificationTypeForChannelInLanguage(
        commonTemplatePreviewDto: CommonTemplatePreviewDto,
    ): String {
        with(commonTemplatePreviewDto) {
            return when (channel) {
                CommunicationChannel.EMAIL -> fromEmailNotificationTypeInLanguage(sourceType, notificationType, language)
                CommunicationChannel.LETTER -> fromLetterNotificationTypeInLanguage(sourceType, notificationType, language)
            }
        }
    }

    private fun fromEmailNotificationTypeInLanguage(
        sourceType: SourceType,
        notificationType: NotificationType,
        language: LanguageDto?,
    ): String {
        val config = getSourceTemplateEmailTemplateConfiguration(sourceType)
        return if (useEnglishTemplate(language)) {
            englishEmail(config, notificationType)
        } else {
            welshEmail(config, notificationType)
        }
    }

    private fun getSourceTemplateEmailTemplateConfiguration(sourceType: SourceType): AbstractNotifyEmailTemplateConfiguration =
        when (sourceType) {
            OVERSEAS -> notifyEmailTemplateConfiguration.overseas
            POSTAL -> notifyEmailTemplateConfiguration.postal
            PROXY -> notifyEmailTemplateConfiguration.proxy
            VOTER_CARD -> notifyEmailTemplateConfiguration.voterCard
            else -> {
                throw NotificationTemplateNotFoundException("No email template configuration defined for sourceType $sourceType")
            }
        }

    private fun useEnglishTemplate(language: LanguageDto?) = language == null || language == ENGLISH

    private fun welshEmail(config: AbstractNotifyEmailTemplateConfiguration, notificationType: NotificationType) =
        when (notificationType) {
            APPLICATION_RECEIVED -> config.receivedWelsh
            APPLICATION_APPROVED -> config.approvedWelsh
            PHOTO_RESUBMISSION -> config.photoResubmissionWelsh
            PHOTO_RESUBMISSION_WITH_REASONS -> config.photoResubmissionWithReasonsWelsh
            ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionWelsh
            ID_DOCUMENT_RESUBMISSION_WITH_REASONS -> config.idDocumentResubmissionWithReasonsWelsh
            ID_DOCUMENT_REQUIRED -> config.idDocumentRequiredWelsh
            REJECTED_DOCUMENT -> config.rejectedDocumentWelsh
            NINO_NOT_MATCHED -> config.ninoNotMatchedWelsh
            NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST -> config.ninoNotMatchedRestrictedDocumentsListWelsh
            REJECTED_PARENT_GUARDIAN -> config.rejectedParentGuardianWelsh
            REJECTED_PREVIOUS_ADDRESS -> config.rejectedPreviousAddressWelsh
            PARENT_GUARDIAN_PROOF_REQUIRED -> config.parentGuardianProofRequiredWelsh
            PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> config.previousAddressDocumentRequiredWelsh
            BESPOKE_COMM -> config.bespokeCommWelsh
            NOT_REGISTERED_TO_VOTE -> config.notRegisteredToVoteWelsh
            SIGNATURE_RESUBMISSION -> config.signatureResubmissionWelsh
            SIGNATURE_RESUBMISSION_WITH_REASONS -> config.signatureResubmissionWithReasonsWelsh
            SIGNATURE_RECEIVED -> config.signatureReceivedWelsh
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
            PHOTO_RESUBMISSION_WITH_REASONS -> config.photoResubmissionWithReasonsEnglish
            ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionEnglish
            ID_DOCUMENT_RESUBMISSION_WITH_REASONS -> config.idDocumentResubmissionWithReasonsEnglish
            ID_DOCUMENT_REQUIRED -> config.idDocumentRequiredEnglish
            REJECTED_DOCUMENT -> config.rejectedDocumentEnglish
            NINO_NOT_MATCHED -> config.ninoNotMatchedEnglish
            NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST -> config.ninoNotMatchedRestrictedDocumentsListEnglish
            REJECTED_PARENT_GUARDIAN -> config.rejectedParentGuardianEnglish
            REJECTED_PREVIOUS_ADDRESS -> config.rejectedPreviousAddressEnglish
            PARENT_GUARDIAN_PROOF_REQUIRED -> config.parentGuardianProofRequiredEnglish
            PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> config.previousAddressDocumentRequiredEnglish
            BESPOKE_COMM -> config.bespokeCommEnglish
            NOT_REGISTERED_TO_VOTE -> config.notRegisteredToVoteEnglish
            SIGNATURE_RESUBMISSION -> config.signatureResubmissionEnglish
            SIGNATURE_RESUBMISSION_WITH_REASONS -> config.signatureResubmissionWithReasonsEnglish
            SIGNATURE_RECEIVED -> config.signatureReceivedEnglish
            else -> {
                throw NotificationTemplateNotFoundException("No email template defined in English for notification type $notificationType and sourceType ${config.sourceType}")
            }
        }
            ?: throw NotificationTemplateNotFoundException("No email template defined in English for notification type $notificationType and sourceType ${config.sourceType}")

    private fun fromLetterNotificationTypeInLanguage(
        sourceType: SourceType,
        notificationType: NotificationType,
        language: LanguageDto?,
    ): String {
        val config = getSourceTemplateLetterTemplateConfiguration(sourceType)
        return if (useEnglishTemplate(language)) {
            englishLetter(config, notificationType)
        } else {
            welshLetter(config, notificationType)
        }
    }

    private fun getSourceTemplateLetterTemplateConfiguration(sourceType: SourceType): AbstractNotifyLetterTemplateConfiguration =
        when (sourceType) {
            POSTAL -> notifyLetterTemplateConfiguration.postal
            VOTER_CARD -> notifyLetterTemplateConfiguration.voterCard
            PROXY -> notifyLetterTemplateConfiguration.proxy
            OVERSEAS -> notifyLetterTemplateConfiguration.overseas
            else -> {
                throw NotificationTemplateNotFoundException("No letter template configuration defined for sourceType $sourceType")
            }
        }

    private fun welshLetter(config: AbstractNotifyLetterTemplateConfiguration, notificationType: NotificationType) =
        when (notificationType) {
            APPLICATION_RECEIVED -> config.receivedWelsh
            APPLICATION_REJECTED -> config.rejectedWelsh
            PHOTO_RESUBMISSION -> config.photoResubmissionWelsh
            PHOTO_RESUBMISSION_WITH_REASONS -> config.photoResubmissionWithReasonsWelsh
            ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionWelsh
            ID_DOCUMENT_RESUBMISSION_WITH_REASONS -> config.idDocumentResubmissionWithReasonsWelsh
            ID_DOCUMENT_REQUIRED -> config.idDocumentRequiredWelsh
            REJECTED_DOCUMENT -> config.rejectedDocumentWelsh
            NINO_NOT_MATCHED -> config.ninoNotMatchedWelsh
            NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST -> config.ninoNotMatchedRestrictedDocumentsListWelsh
            REJECTED_PARENT_GUARDIAN -> config.rejectedParentGuardianWelsh
            REJECTED_PREVIOUS_ADDRESS -> config.rejectedPreviousAddressWelsh
            PARENT_GUARDIAN_PROOF_REQUIRED -> config.parentGuardianProofRequiredWelsh
            PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> config.previousAddressDocumentRequiredWelsh
            BESPOKE_COMM -> config.bespokeCommWelsh
            NOT_REGISTERED_TO_VOTE -> config.notRegisteredToVoteWelsh
            SIGNATURE_RESUBMISSION -> config.signatureResubmissionWelsh
            SIGNATURE_RESUBMISSION_WITH_REASONS -> config.signatureResubmissionWithReasonsWelsh
            else -> {
                throw NotificationTemplateNotFoundException("No letter template defined in Welsh for notification type $notificationType and sourceType ${config.sourceType}")
            }
        }
            ?: throw NotificationTemplateNotFoundException("No letter template defined in Welsh for notification type $notificationType and sourceType ${config.sourceType}")

    private fun englishLetter(
        config: AbstractNotifyLetterTemplateConfiguration,
        notificationType: NotificationType,
    ): String = when (notificationType) {
        APPLICATION_RECEIVED -> config.receivedEnglish
        APPLICATION_REJECTED -> config.rejectedEnglish
        PHOTO_RESUBMISSION -> config.photoResubmissionEnglish
        PHOTO_RESUBMISSION_WITH_REASONS -> config.photoResubmissionWithReasonsEnglish
        ID_DOCUMENT_RESUBMISSION -> config.idDocumentResubmissionEnglish
        ID_DOCUMENT_RESUBMISSION_WITH_REASONS -> config.idDocumentResubmissionWithReasonsEnglish
        ID_DOCUMENT_REQUIRED -> config.idDocumentRequiredEnglish
        REJECTED_DOCUMENT -> config.rejectedDocumentEnglish
        NINO_NOT_MATCHED -> config.ninoNotMatchedEnglish
        NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST -> config.ninoNotMatchedRestrictedDocumentsListEnglish
        REJECTED_PARENT_GUARDIAN -> config.rejectedParentGuardianEnglish
        REJECTED_PREVIOUS_ADDRESS -> config.rejectedPreviousAddressEnglish
        PARENT_GUARDIAN_PROOF_REQUIRED -> config.parentGuardianProofRequiredEnglish
        PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> config.previousAddressDocumentRequiredEnglish
        BESPOKE_COMM -> config.bespokeCommEnglish
        NOT_REGISTERED_TO_VOTE -> config.notRegisteredToVoteEnglish
        SIGNATURE_RESUBMISSION -> config.signatureResubmissionEnglish
        SIGNATURE_RESUBMISSION_WITH_REASONS -> config.signatureResubmissionWithReasonsEnglish
        else -> {
            throw NotificationTemplateNotFoundException("No letter template defined in English for notification type $notificationType and sourceType ${config.sourceType}")
        }
    }
        ?: throw NotificationTemplateNotFoundException("No letter template defined in English for notification type $notificationType and sourceType ${config.sourceType}")
}
