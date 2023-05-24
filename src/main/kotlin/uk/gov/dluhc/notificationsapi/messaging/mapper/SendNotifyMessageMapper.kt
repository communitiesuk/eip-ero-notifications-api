package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.apache.commons.lang3.StringUtils.isNotBlank
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationChannelMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage

@Mapper(
    uses = [
        LanguageMapper::class,
        SourceTypeMapper::class,
        NotificationChannelMapper::class,
        NotificationTypeMapper::class,
        NotificationDestinationDtoMapper::class
    ]
)
abstract class SendNotifyMessageMapper {

    @Mapping(target = "notificationType", expression = "java( photoResubmissionNotificationType(message) )")
    abstract fun fromPhotoMessageToSendNotificationRequestDto(
        message: SendNotifyPhotoResubmissionMessage,
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", expression = "java( idDocumentResubmissionNotificationType(message) )")
    abstract fun fromIdDocumentMessageToSendNotificationRequestDto(
        message: SendNotifyIdDocumentResubmissionMessage,
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

    protected fun photoResubmissionNotificationType(message: SendNotifyPhotoResubmissionMessage): NotificationType =
        // PHOTO_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(message.personalisation) {
            if (photoRejectionReasonsExcludingOther.isNotEmpty() || isNotBlank(photoRejectionNotes))
                PHOTO_RESUBMISSION_WITH_REASONS
            else
                PHOTO_RESUBMISSION
        }

    protected fun idDocumentResubmissionNotificationType(message: SendNotifyIdDocumentResubmissionMessage): NotificationType =
        // ID_DOCUMENT_RESUBMISSION_WITH_REASONS should be used if all rejected documents have either any rejection reasons (excluding OTHER)
        // or has rejection notes
        with(message.personalisation) {
            if (rejectedDocuments.isNotEmpty() &&
                rejectedDocuments.all { it.rejectionReasonsExcludingOther.isNotEmpty() || isNotBlank(it.rejectionNotes) }
            )
                ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            else
                ID_DOCUMENT_RESUBMISSION
        }
}
