package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationNameValue
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@Mapper(uses = [LanguageMapper::class])
abstract class SendNotifyMessageMapper {

    @Mapping(target = "notificationType", source = "messageType")
    @Mapping(target = "emailAddress", source = "message.toAddress.emailAddress")
    abstract fun toSendNotificationRequestDto(message: SendNotifyPhotoResubmissionMessage): SendNotificationRequestDto

    @ValueMapping(target = "VOTER_CARD", source = "VOTER_MINUS_CARD")
    protected abstract fun mapToSourceType(sourceType: SqsSourceType): SourceType

    @ValueMapping(target = "APPLICATION_RECEIVED", source = "APPLICATION_MINUS_RECEIVED")
    @ValueMapping(target = "APPLICATION_APPROVED", source = "APPLICATION_MINUS_APPROVED")
    @ValueMapping(target = "APPLICATION_REJECTED", source = "APPLICATION_MINUS_REJECTED")
    @ValueMapping(target = "PHOTO_RESUBMISSION", source = "PHOTO_MINUS_RESUBMISSION")
    protected abstract fun mapToNotificationType(messageType: MessageType): NotificationType

    fun map(placeholders: List<TemplatePersonalisationNameValue>): Map<String, String> =
        placeholders.associate { it.name to it.value }
}
