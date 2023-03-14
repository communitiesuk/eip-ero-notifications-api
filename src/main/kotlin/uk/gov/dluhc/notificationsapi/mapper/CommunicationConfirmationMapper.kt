package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import java.time.LocalDateTime
import java.util.UUID

@Mapper(
    uses = [CommunicationConfirmationReasonMapper::class, CommunicationConfirmationChannelMapper::class, SourceTypeMapper::class],
    imports = [UUID::class, LocalDateTime::class],
)
interface CommunicationConfirmationMapper {

    @Mapping(target = "sourceType", constant = "ANONYMOUS_ELECTOR_DOCUMENT")
    @Mapping(target = "sentAt", expression = "java( LocalDateTime.now() )")
    fun fromApiToDto(
        eroId: String,
        sourceReference: String,
        requestor: String,
        request: CreateOfflineCommunicationConfirmationRequest
    ): CommunicationConfirmationDto

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    fun fromDtoToEntity(dto: CommunicationConfirmationDto): CommunicationConfirmation
}
