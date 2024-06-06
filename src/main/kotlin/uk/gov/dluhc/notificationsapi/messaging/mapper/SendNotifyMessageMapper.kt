package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_SIGNATURE
import uk.gov.dluhc.notificationsapi.dto.NotificationType.REJECTED_SIGNATURE_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.mapper.DocumentCategoryMapper
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationChannelMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyNinoNotMatchedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedDocumentMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedSignatureMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRequestedSignatureMessage

@Mapper(
    componentModel = "spring",
    uses = [
        LanguageMapper::class,
        SourceTypeMapper::class,
        NotificationChannelMapper::class,
        NotificationTypeMapper::class,
        NotificationDestinationDtoMapper::class,
    ],
)
abstract class SendNotifyMessageMapper {

    @Autowired
    private lateinit var documentCategoryMapper: DocumentCategoryMapper

    @Mapping(target = "notificationType", expression = "java( photoResubmissionNotificationType(message) )")
    abstract fun fromPhotoMessageToSendNotificationRequestDto(
        message: SendNotifyPhotoResubmissionMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", expression = "java( idDocumentResubmissionNotificationType(message) )")
    abstract fun fromIdDocumentMessageToSendNotificationRequestDto(
        message: SendNotifyIdDocumentResubmissionMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", expression = "java( rejectedSignatureNotificationType(message) )")
    abstract fun fromRejectedSignatureToSendNotificationRequestDto(
        message: SendNotifyRejectedSignatureMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", source = "messageType")
    abstract fun fromRequestedSignatureToSendNotificationRequestDto(
        message: SendNotifyRequestedSignatureMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", source = "messageType")
    abstract fun fromIdDocumentRequiredMessageToSendNotificationRequestDto(
        message: SendNotifyIdDocumentRequiredMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "EMAIL")
    @Mapping(target = "notificationType", source = "messageType")
    abstract fun fromReceivedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationReceivedMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "EMAIL")
    @Mapping(target = "notificationType", source = "messageType")
    abstract fun fromApprovedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationApprovedMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "LETTER")
    @Mapping(target = "notificationType", source = "messageType")
    abstract fun fromRejectedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationRejectedMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", expression = "java( rejectedDocumentNotificationType(message) )")
    abstract fun fromRejectedDocumentMessageToSendNotificationRequestDto(message: SendNotifyRejectedDocumentMessage): SendNotificationRequestDto

    @Mapping(
        target = "notificationType",
        expression = "java( requiredDocumentNotificationType(message) )",
    )
    abstract fun fromRequiredDocumentMessageToSendNotificationRequestDto(
        message: SendNotifyNinoNotMatchedMessage,
    ): SendNotificationRequestDto

    protected fun photoResubmissionNotificationType(message: SendNotifyPhotoResubmissionMessage): NotificationType =
        // PHOTO_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(message.personalisation) {
            if (photoRejectionReasonsExcludingOther.isNotEmpty() || !photoRejectionNotes.isNullOrBlank()) {
                PHOTO_RESUBMISSION_WITH_REASONS
            } else {
                PHOTO_RESUBMISSION
            }
        }

    // ID_DOCUMENT_RESUBMISSION_WITH_REASONS should be used if any rejected documents have either any rejection reasons (excluding OTHER)
    // or has rejection notes
    protected fun idDocumentResubmissionNotificationType(message: SendNotifyIdDocumentResubmissionMessage): NotificationType =
        with(message.personalisation) {
            if (rejectedDocuments.isNotEmpty() &&
                rejectedDocuments.any { it.rejectionReasonsExcludingOther.isNotEmpty() || !it.rejectionNotes.isNullOrBlank() }
            ) {
                ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            } else {
                ID_DOCUMENT_RESUBMISSION
            }
        }

    // REJECTED_SIGNATURE_WITH_REASONS should be used if any there are either any rejection reasons (excluding OTHER)
    // or any rejection notes
    protected fun rejectedSignatureNotificationType(message: SendNotifyRejectedSignatureMessage): NotificationType =
        with(message.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrBlank()) {
                REJECTED_SIGNATURE_WITH_REASONS
            } else {
                REJECTED_SIGNATURE
            }
        }

    protected fun rejectedDocumentNotificationType(
        message: SendNotifyRejectedDocumentMessage,
    ): NotificationType =
        with(message) {
            val documentCategoryDto = documentCategoryMapper.fromApiMessageToDto(documentCategory)
            documentCategoryMapper.fromRejectedDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)
        }

    protected fun requiredDocumentNotificationType(
        message: SendNotifyNinoNotMatchedMessage,
    ): NotificationType =
        with(message) {
            val documentCategoryDto = documentCategoryMapper.fromApiMessageToDto(documentCategory)
            if (documentCategoryDto == DocumentCategoryDto.IDENTITY && hasRestrictedDocumentsList) {
                NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST
            } else {
                documentCategoryMapper.fromRequiredDocumentCategoryDtoToNotificationTypeDto(documentCategoryDto)
            }
        }

    @Mapping(source = "messageType", target = "notificationType")
    abstract fun fromRejectedSignatureMessageToSendNotificationRequestDto(
        message: SendNotifyRejectedSignatureMessage,
    ): SendNotificationRequestDto
}
