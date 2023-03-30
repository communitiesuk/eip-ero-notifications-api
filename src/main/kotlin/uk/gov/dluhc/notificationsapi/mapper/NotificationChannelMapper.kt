package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.database.entity.Channel as ChannelEntity
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel as NotificationChannelMessagingApi
import uk.gov.dluhc.notificationsapi.models.NotificationChannel as NotificationChannelApi

@Mapper
interface NotificationChannelMapper {

    fun fromApiToDto(apiLanguageEnum: NotificationChannelApi): NotificationChannelDto

    fun fromDtoToApi(messageLanguageEnum: NotificationChannelDto): NotificationChannelApi

    fun fromEntityToDto(entityEnum: ChannelEntity): NotificationChannelDto

    fun fromMessagingApiToDto(messagingApiLanguageEnum: NotificationChannelMessagingApi): NotificationChannelDto
}
