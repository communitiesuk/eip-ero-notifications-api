package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.database.entity.Channel as ChannelEntity
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel as CommunicationChannelMessagingApi
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel as CommunicationChannelApi

@Mapper
interface CommunicationChannelMapper {

    fun fromApiToDto(apiCommunicationChannel: CommunicationChannelApi): CommunicationChannelDto

    fun fromDtoToApi(communicationChannelDto: CommunicationChannelDto): CommunicationChannelApi

    fun fromEntityToDto(entityEnum: ChannelEntity): CommunicationChannelDto

    fun fromMessagingApiToDto(messagingApiLanguageEnum: CommunicationChannelMessagingApi): CommunicationChannelDto
}
