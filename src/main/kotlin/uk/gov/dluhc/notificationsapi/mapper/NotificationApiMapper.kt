package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.models.SentCommunicationResponse
import uk.gov.dluhc.notificationsapi.database.entity.Notification as NotificationEntity

@Mapper
interface NotificationApiMapper {

    @Mapping(target = "subject", source = "notifyDetails.subject")
    @Mapping(target = "body", source = "notifyDetails.body")
    fun toSentCommunicationsApi(notificationEntity: NotificationEntity): SentCommunicationResponse
}
