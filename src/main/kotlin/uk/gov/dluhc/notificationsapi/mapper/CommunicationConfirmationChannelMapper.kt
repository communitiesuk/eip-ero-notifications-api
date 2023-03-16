package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel

@Mapper
interface CommunicationConfirmationChannelMapper {

    fun fromApiToDto(apiChannelEnum: OfflineCommunicationChannel): CommunicationConfirmationChannelDto

    fun fromDtoToApi(dtoChannelEnum: CommunicationConfirmationChannelDto): OfflineCommunicationChannel

    fun fromEntityToDto(entityChannelEnum: CommunicationConfirmationChannel): CommunicationConfirmationChannelDto

    fun fromDtoToEntity(dtoChannelEnum: CommunicationConfirmationChannelDto): CommunicationConfirmationChannel
}
