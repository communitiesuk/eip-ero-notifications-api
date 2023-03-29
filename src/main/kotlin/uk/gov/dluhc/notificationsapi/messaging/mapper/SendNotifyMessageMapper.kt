package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationRejectedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class, NotificationTypeMapper::class, NotificationDestinationDtoMapper::class])
interface SendNotifyMessageMapper {

    @Mapping(target = "notificationType", source = "messageType")
    fun fromPhotoMessageToSendNotificationRequestDto(
        message: SendNotifyPhotoResubmissionMessage
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", source = "messageType")
    fun fromIdDocumentMessageToSendNotificationRequestDto(
        message: SendNotifyIdDocumentResubmissionMessage
    ): SendNotificationRequestDto

    @Mapping(target = "notificationType", source = "messageType")
    fun fromIdDocumentRequiredMessageToSendNotificationRequestDto(
        message: SendNotifyIdDocumentRequiredMessage
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "EMAIL")
    @Mapping(target = "notificationType", source = "messageType")
    fun fromReceivedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationReceivedMessage
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "EMAIL")
    @Mapping(target = "notificationType", source = "messageType")
    fun fromApprovedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationApprovedMessage
    ): SendNotificationRequestDto

    @Mapping(target = "channel", constant = "LETTER")
    @Mapping(target = "notificationType", source = "messageType")
    fun fromRejectedMessageToSendNotificationRequestDto(
        message: SendNotifyApplicationRejectedMessage
    ): SendNotificationRequestDto
}
