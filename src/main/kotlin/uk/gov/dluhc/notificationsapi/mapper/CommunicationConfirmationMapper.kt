package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.models.CommunicationConfirmationHistoryEntry
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID

@Mapper(
    uses = [
        CommunicationConfirmationReasonMapper::class,
        CommunicationConfirmationChannelMapper::class,
        SourceTypeMapper::class,
        DateTimeMapper::class,
    ],
    imports = [UUID::class, LocalDateTime::class],
)
abstract class CommunicationConfirmationMapper {

    @Autowired
    protected lateinit var clock: Clock

    @Mapping(target = "sourceType", constant = "ANONYMOUS_ELECTOR_DOCUMENT")
    @Mapping(target = "sentAt", expression = "java( LocalDateTime.now(clock) )")
    abstract fun fromApiToDto(
        eroId: String,
        sourceReference: String,
        requestor: String,
        request: CreateOfflineCommunicationConfirmationRequest,
    ): CommunicationConfirmationDto

    @Mapping(target = "timestamp", source = "sentAt")
    abstract fun fromDtoToApi(dto: CommunicationConfirmationDto): CommunicationConfirmationHistoryEntry

    abstract fun fromDtosToApis(dtos: List<CommunicationConfirmationDto>): List<CommunicationConfirmationHistoryEntry>

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    abstract fun fromDtoToEntity(dto: CommunicationConfirmationDto): CommunicationConfirmation

    @InheritInverseConfiguration
    abstract fun fromEntityToDto(entity: CommunicationConfirmation): CommunicationConfirmationDto

    abstract fun fromEntitiesToDtos(entities: List<CommunicationConfirmation>): List<CommunicationConfirmationDto>
}
