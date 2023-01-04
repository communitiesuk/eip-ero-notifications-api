package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.TemplateType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity

@Mapper
interface NotificationTypeMapper {
    fun toNotificationType(templateType: TemplateType): NotificationType

    @ValueMapping(target = "APPLICATION_RECEIVED", source = "APPLICATION_MINUS_RECEIVED")
    @ValueMapping(target = "APPLICATION_APPROVED", source = "APPLICATION_MINUS_APPROVED")
    @ValueMapping(target = "APPLICATION_REJECTED", source = "APPLICATION_MINUS_REJECTED")
    @ValueMapping(target = "PHOTO_RESUBMISSION", source = "PHOTO_MINUS_RESUBMISSION")
    @ValueMapping(target = "ID_DOCUMENT_RESUBMISSION", source = "ID_MINUS_DOCUMENT_MINUS_RESUBMISSION")
    fun mapMessageTypeToNotificationType(messageType: MessageType): NotificationType

    fun toNotificationTypeEntity(notificationType: NotificationType): NotificationTypeEntity
}
