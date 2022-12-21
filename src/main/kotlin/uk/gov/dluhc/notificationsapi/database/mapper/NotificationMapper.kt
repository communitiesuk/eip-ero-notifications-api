package uk.gov.dluhc.notificationsapi.database.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionPersonalisationMapper
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as EntityNotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as EntitySourceType

@Mapper
abstract class NotificationMapper {

    @Autowired
    protected lateinit var photoResubmissionPersonalisationMapper: PhotoResubmissionPersonalisationMapper

    @Mapping(target = "id", source = "notificationId")
    @Mapping(target = "type", source = "request.notificationType")
    @Mapping(target = "gssCode", source = "request.gssCode")
    @Mapping(target = "requestor", source = "request.requestor")
    @Mapping(target = "sourceReference", source = "request.sourceReference")
    @Mapping(target = "sourceType", source = "request.sourceType")
    @Mapping(target = "toEmail", source = "request.emailAddress")
    @Mapping(target = "personalisation", expression = "java(photoResubmissionPersonalisationMapper.toTemplatePersonalisationMap(request.getPersonalisation()))")
    @Mapping(target = "notifyDetails", source = "sendNotificationResponse")
    @Mapping(target = "sentAt", source = "sentAt")
    abstract fun createNotification(
        notificationId: UUID,
        request: SendNotificationRequestDto,
        sendNotificationResponse: SendNotificationResponseDto,
        sentAt: LocalDateTime
    ): Notification

    abstract fun toSourceType(sourceType: SourceType): EntitySourceType

    abstract fun toNotificationType(notificationType: NotificationType): EntityNotificationType

    abstract fun toNotifyDetails(sendNotificationResponse: SendNotificationResponseDto): NotifyDetails
}
