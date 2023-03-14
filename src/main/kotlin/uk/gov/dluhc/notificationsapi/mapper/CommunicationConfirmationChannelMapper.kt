package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel as OfflineCommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel as OfflineCommunicationChannelApi

@Mapper
interface CommunicationConfirmationChannelMapper {

    fun fromApiToDto(apiChannelEnum: OfflineCommunicationChannelApi): CommunicationConfirmationChannelDto

    fun fromDtoToEntity(dtoEnum: CommunicationConfirmationChannelDto): OfflineCommunicationChannelEntity
}
