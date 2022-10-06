package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.models.TemplateType

@Mapper
interface NotificationTypeMapper {
    @ValueMapping(source = "APPLICATION_MINUS_RECEIVED", target = "APPLICATION_RECEIVED")
    @ValueMapping(source = "APPLICATION_MINUS_APPROVED", target = "APPLICATION_APPROVED")
    @ValueMapping(source = "APPLICATION_MINUS_REJECTED", target = "APPLICATION_REJECTED")
    @ValueMapping(source = "PHOTO_MINUS_RESUBMISSION", target = "PHOTO_RESUBMISSION")
    fun toNotificationType(templateType: TemplateType): NotificationType
}
