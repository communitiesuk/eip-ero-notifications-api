package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity

@Component
class NotificationTypeMapper {

    fun mapMessageTypeToNotificationType(messageType: MessageType): NotificationType =
        when (messageType) {
            MessageType.APPLICATION_MINUS_RECEIVED -> NotificationType.APPLICATION_RECEIVED
            MessageType.APPLICATION_MINUS_APPROVED -> NotificationType.APPLICATION_APPROVED
            MessageType.APPLICATION_MINUS_REJECTED -> NotificationType.APPLICATION_REJECTED
            MessageType.PHOTO_MINUS_RESUBMISSION -> NotificationType.PHOTO_RESUBMISSION
            MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION -> NotificationType.ID_DOCUMENT_RESUBMISSION
            MessageType.ID_MINUS_DOCUMENT_MINUS_REQUIRED -> NotificationType.ID_DOCUMENT_REQUIRED
            MessageType.REJECTED_MINUS_SIGNATURE -> NotificationType.REJECTED_SIGNATURE
            MessageType.REQUESTED_MINUS_SIGNATURE -> NotificationType.REQUESTED_SIGNATURE
            MessageType.REJECTED_MINUS_DOCUMENT -> NotificationType.REJECTED_DOCUMENT
            MessageType.NINO_MINUS_NOT_MINUS_MATCHED -> NotificationType.NINO_NOT_MATCHED
            MessageType.PARENT_MINUS_GUARDIAN_MINUS_PROOF_MINUS_REQUIRED -> NotificationType.PARENT_GUARDIAN_PROOF_REQUIRED
            MessageType.PREVIOUS_MINUS_ADDRESS_MINUS_DOCUMENT_MINUS_REQUIRED -> NotificationType.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED
            MessageType.BESPOKE_MINUS_COMM -> NotificationType.BESPOKE_COMM
            MessageType.NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE -> NotificationType.NOT_REGISTERED_TO_VOTE
            MessageType.SIGNATURE_MINUS_RESUBMISSION -> NotificationType.SIGNATURE_RESUBMISSION
            MessageType.SIGNATURE_MINUS_RECEIVED -> NotificationType.SIGNATURE_RECEIVED
        }

    // <TEMPLATE_NAME>_WITH_REASONS is an implementation detail and not a "business" notification type
    // Therefore it should be saved to the database as <TEMPLATE_NAME>
    // Similarly NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST
    fun toNotificationTypeEntity(notificationType: NotificationType): NotificationTypeEntity =
        when (notificationType) {
            NotificationType.APPLICATION_RECEIVED -> NotificationTypeEntity.APPLICATION_RECEIVED
            NotificationType.APPLICATION_APPROVED -> NotificationTypeEntity.APPLICATION_APPROVED
            NotificationType.APPLICATION_REJECTED -> NotificationTypeEntity.APPLICATION_REJECTED
            NotificationType.PHOTO_RESUBMISSION,
            NotificationType.PHOTO_RESUBMISSION_WITH_REASONS,
            -> NotificationTypeEntity.PHOTO_RESUBMISSION
            NotificationType.ID_DOCUMENT_RESUBMISSION,
            NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
            -> NotificationTypeEntity.ID_DOCUMENT_RESUBMISSION
            NotificationType.ID_DOCUMENT_REQUIRED -> NotificationTypeEntity.ID_DOCUMENT_REQUIRED
            NotificationType.REJECTED_DOCUMENT -> NotificationTypeEntity.REJECTED_DOCUMENT
            NotificationType.REJECTED_SIGNATURE,
            NotificationType.REJECTED_SIGNATURE_WITH_REASONS,
            -> NotificationTypeEntity.REJECTED_SIGNATURE
            NotificationType.REQUESTED_SIGNATURE -> NotificationTypeEntity.REQUESTED_SIGNATURE
            NotificationType.NINO_NOT_MATCHED,
            NotificationType.NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST,
            -> NotificationTypeEntity.NINO_NOT_MATCHED
            NotificationType.REJECTED_PARENT_GUARDIAN -> NotificationTypeEntity.REJECTED_PARENT_GUARDIAN
            NotificationType.REJECTED_PREVIOUS_ADDRESS -> NotificationTypeEntity.REJECTED_PREVIOUS_ADDRESS
            NotificationType.PARENT_GUARDIAN_PROOF_REQUIRED -> NotificationTypeEntity.PARENT_GUARDIAN_PROOF_REQUIRED
            NotificationType.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> NotificationTypeEntity.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED
            NotificationType.BESPOKE_COMM -> NotificationTypeEntity.BESPOKE_COMM
            NotificationType.NOT_REGISTERED_TO_VOTE -> NotificationTypeEntity.NOT_REGISTERED_TO_VOTE
            NotificationType.SIGNATURE_RESUBMISSION,
            NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS,
            -> NotificationTypeEntity.SIGNATURE_RESUBMISSION
            NotificationType.SIGNATURE_RECEIVED -> NotificationTypeEntity.SIGNATURE_RECEIVED
        }

    fun toNotificationTypeDto(notificationTypeEntity: NotificationTypeEntity): NotificationType =
        when (notificationTypeEntity) {
            NotificationTypeEntity.APPLICATION_RECEIVED -> NotificationType.APPLICATION_RECEIVED
            NotificationTypeEntity.APPLICATION_APPROVED -> NotificationType.APPLICATION_APPROVED
            NotificationTypeEntity.APPLICATION_REJECTED -> NotificationType.APPLICATION_REJECTED
            NotificationTypeEntity.PHOTO_RESUBMISSION -> NotificationType.PHOTO_RESUBMISSION
            NotificationTypeEntity.ID_DOCUMENT_RESUBMISSION -> NotificationType.ID_DOCUMENT_RESUBMISSION
            NotificationTypeEntity.ID_DOCUMENT_REQUIRED -> NotificationType.ID_DOCUMENT_REQUIRED
            NotificationTypeEntity.REJECTED_DOCUMENT -> NotificationType.REJECTED_DOCUMENT
            NotificationTypeEntity.REJECTED_SIGNATURE -> NotificationType.REJECTED_SIGNATURE
            NotificationTypeEntity.REQUESTED_SIGNATURE -> NotificationType.REQUESTED_SIGNATURE
            NotificationTypeEntity.NINO_NOT_MATCHED -> NotificationType.NINO_NOT_MATCHED
            NotificationTypeEntity.REJECTED_PARENT_GUARDIAN -> NotificationType.REJECTED_PARENT_GUARDIAN
            NotificationTypeEntity.REJECTED_PREVIOUS_ADDRESS -> NotificationType.REJECTED_PREVIOUS_ADDRESS
            NotificationTypeEntity.PARENT_GUARDIAN_PROOF_REQUIRED -> NotificationType.PARENT_GUARDIAN_PROOF_REQUIRED
            NotificationTypeEntity.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> NotificationType.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED
            NotificationTypeEntity.BESPOKE_COMM -> NotificationType.BESPOKE_COMM
            NotificationTypeEntity.NOT_REGISTERED_TO_VOTE -> NotificationType.NOT_REGISTERED_TO_VOTE
            NotificationTypeEntity.SIGNATURE_RESUBMISSION -> NotificationType.SIGNATURE_RESUBMISSION
            NotificationTypeEntity.SIGNATURE_RECEIVED -> NotificationType.SIGNATURE_RECEIVED
        }

    fun fromNotificationTypeDtoToTemplateTypeApi(notificationType: NotificationType): TemplateType =
        when (notificationType) {
            NotificationType.APPLICATION_RECEIVED -> TemplateType.APPLICATION_MINUS_RECEIVED
            NotificationType.APPLICATION_APPROVED -> TemplateType.APPLICATION_MINUS_APPROVED
            NotificationType.APPLICATION_REJECTED -> TemplateType.APPLICATION_MINUS_REJECTED
            NotificationType.PHOTO_RESUBMISSION -> TemplateType.PHOTO_MINUS_RESUBMISSION
            NotificationType.ID_DOCUMENT_RESUBMISSION -> TemplateType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION
            NotificationType.ID_DOCUMENT_REQUIRED -> TemplateType.ID_MINUS_DOCUMENT_MINUS_REQUIRED
            NotificationType.REJECTED_DOCUMENT -> TemplateType.REJECTED_MINUS_DOCUMENT
            NotificationType.REJECTED_SIGNATURE -> TemplateType.REJECTED_MINUS_SIGNATURE
            NotificationType.REQUESTED_SIGNATURE -> TemplateType.REQUESTED_MINUS_SIGNATURE
            NotificationType.NINO_NOT_MATCHED -> TemplateType.NINO_MINUS_NOT_MINUS_MATCHED
            NotificationType.REJECTED_PARENT_GUARDIAN -> TemplateType.REJECTED_MINUS_PARENT_MINUS_GUARDIAN
            NotificationType.REJECTED_PREVIOUS_ADDRESS -> TemplateType.REJECTED_MINUS_PREVIOUS_MINUS_ADDRESS
            NotificationType.PARENT_GUARDIAN_PROOF_REQUIRED -> TemplateType.PARENT_MINUS_GUARDIAN_MINUS_PROOF_MINUS_REQUIRED
            NotificationType.PREVIOUS_ADDRESS_DOCUMENT_REQUIRED -> TemplateType.PREVIOUS_MINUS_ADDRESS_MINUS_DOCUMENT_MINUS_REQUIRED
            NotificationType.BESPOKE_COMM -> TemplateType.BESPOKE_MINUS_COMM
            NotificationType.NOT_REGISTERED_TO_VOTE -> TemplateType.NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE
            NotificationType.SIGNATURE_RESUBMISSION -> TemplateType.SIGNATURE_MINUS_RESUBMISSION
            NotificationType.SIGNATURE_RECEIVED -> TemplateType.SIGNATURE_MINUS_RECEIVED
            else -> throw IllegalArgumentException("Unexpected NotificationType $notificationType when mapping to API TemplateType")
        }
}
