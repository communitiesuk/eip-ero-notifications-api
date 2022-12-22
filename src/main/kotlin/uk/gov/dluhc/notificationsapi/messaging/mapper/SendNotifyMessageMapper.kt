package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class, NotificationTypeMapper::class])
interface SendNotifyMessageMapper {

    @Mapping(target = "notificationType", source = "messageType")
    @Mapping(target = "emailAddress", source = "message.toAddress.emailAddress")
    fun toSendNotificationRequestDto(
        message: SendNotifyPhotoResubmissionMessage
    ): SendNotificationRequestDto
}
