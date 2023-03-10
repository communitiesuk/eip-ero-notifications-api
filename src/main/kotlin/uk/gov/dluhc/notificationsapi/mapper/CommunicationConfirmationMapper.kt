package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.dto.CreateOfflineCommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@Mapper(
    uses = [CommunicationConfirmationReasonMapper::class, CommunicationConfirmationChannelMapper::class, SourceTypeMapper::class],
    imports = [UUID::class],
)
interface CommunicationConfirmationMapper {

    fun fromApiToDto(
        eroId: String,
        sourceReference: String,
        sourceType: SourceTypeDto,
        requestor: String,
        sentAt: LocalDateTime,
        request: CreateOfflineCommunicationConfirmationRequest
    ): CreateOfflineCommunicationConfirmationDto

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    fun fromDtoToEntity(dto: CreateOfflineCommunicationConfirmationDto): CommunicationConfirmation
}
