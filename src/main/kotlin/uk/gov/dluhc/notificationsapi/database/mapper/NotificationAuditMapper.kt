package uk.gov.dluhc.notificationsapi.database.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotificationAudit

@Mapper
abstract class NotificationAuditMapper {
    @Mapping(target = "templateDetails", source = "notifyDetails")
    abstract fun createNotificationAudit(notification: Notification): NotificationAudit
}
