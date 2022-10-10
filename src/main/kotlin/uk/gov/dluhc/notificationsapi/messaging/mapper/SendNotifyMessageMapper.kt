package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyMessage
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationInner
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@Mapper
abstract class SendNotifyMessageMapper {

    @Mapping(source = "messageType", target = "notificationType", qualifiedByName = ["mapMessageType"])
    @Mapping(source = "sendNotifyMessage.toAddress.emailAddress", target = "emailAddress")
    abstract fun toSendNotificationRequestDto(sendNotifyMessage: SendNotifyMessage): SendNotificationRequestDto

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    abstract fun map(sourceType: SqsSourceType): SourceType

    @ValueMapping(source = "APPLICATION_MINUS_RECEIVED", target = "APPLICATION_RECEIVED")
    @ValueMapping(source = "APPLICATION_MINUS_APPROVED", target = "APPLICATION_APPROVED")
    @ValueMapping(source = "APPLICATION_MINUS_REJECTED", target = "APPLICATION_REJECTED")
    @ValueMapping(source = "PHOTO_MINUS_RESUBMISSION", target = "PHOTO_RESUBMISSION")
    @Named("mapMessageType")
    abstract fun map(messageType: MessageType): NotificationType

    fun map(placeholders: List<TemplatePersonalisationInner>): Map<String, String> =
        placeholders.associate { it.name!! to it.value!! }
}
