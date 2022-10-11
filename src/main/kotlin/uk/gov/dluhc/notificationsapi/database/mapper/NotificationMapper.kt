package uk.gov.dluhc.notificationsapi.database.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as EntityNotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as EntitySourceType

@Mapper
interface NotificationMapper {

    @Mapping(source = "notificationId", target = "id")
    @Mapping(source = "request.notificationType", target = "type")
    @Mapping(source = "request.gssCode", target = "gssCode")
    @Mapping(source = "request.requestor", target = "requestor")
    @Mapping(source = "request.sourceReference", target = "sourceReference")
    @Mapping(source = "request.sourceType", target = "sourceType")
    @Mapping(source = "request.emailAddress", target = "toEmail")
    @Mapping(source = "request.personalisation", target = "personalisation")
    @Mapping(source = "sendNotificationResponse", target = "notifyDetails")
    @Mapping(source = "sentAt", target = "sentAt")
    fun createNotification(
        notificationId: UUID,
        request: SendNotificationRequestDto,
        sendNotificationResponse: SendNotificationResponseDto,
        sentAt: LocalDateTime
    ): Notification

    fun toSourceType(sourceType: SourceType): EntitySourceType

    fun toNotificationType(notificationType: NotificationType): EntityNotificationType

    fun toNotifyDetails(sendNotificationResponse: SendNotificationResponseDto): NotifyDetails
}
