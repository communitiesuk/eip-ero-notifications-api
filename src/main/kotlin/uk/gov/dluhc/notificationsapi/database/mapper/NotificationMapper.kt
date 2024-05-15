package uk.gov.dluhc.notificationsapi.database.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.dluhc.notificationsapi.database.entity.PostalAddress as EntityPostalAddress
import uk.gov.dluhc.notificationsapi.dto.PostalAddress as DtoPostalAddress

@Mapper(uses = [SourceTypeMapper::class, NotificationTypeMapper::class])
abstract class NotificationMapper {
    @Mapping(target = "id", source = "notificationId")
    @Mapping(target = "type", source = "request.notificationType")
    @Mapping(target = "gssCode", source = "request.gssCode")
    @Mapping(target = "requestor", source = "request.requestor")
    @Mapping(target = "sourceReference", source = "request.sourceReference")
    @Mapping(target = "sourceType", source = "request.sourceType")
    @Mapping(target = "toEmail", source = "request.toAddress.emailAddress")
    @Mapping(target = "toPostalAddress", source = "request.toAddress.postalAddress")
    @Mapping(target = "personalisation", source = "personalisation")
    @Mapping(target = "notifyDetails", source = "sendNotificationResponse")
    @Mapping(target = "sentAt", source = "sentAt")
    @Mapping(target = "channel", source = "request.channel")
    abstract fun createNotification(
        notificationId: UUID,
        request: SendNotificationRequestDto,
        personalisation: Map<String, Any>?,
        sendNotificationResponse: SendNotificationResponseDto?,
        sentAt: LocalDateTime
    ): Notification

    abstract fun toPostalAddress(postalAddress: DtoPostalAddress): EntityPostalAddress
    abstract fun toNotifyDetails(sendNotificationResponse: SendNotificationResponseDto): NotifyDetails
}
