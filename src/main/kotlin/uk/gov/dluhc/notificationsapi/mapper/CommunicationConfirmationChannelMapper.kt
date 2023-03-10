package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationChannelDto
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel as OfflineCommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel as OfflineCommunicationChannelApi

@Mapper
interface CommunicationConfirmationChannelMapper {

    fun fromApiToDto(apiChannelEnum: OfflineCommunicationChannelApi): OfflineCommunicationChannelDto

    fun fromDtoToEntity(dtoEnum: OfflineCommunicationChannelDto): OfflineCommunicationChannelEntity
}
