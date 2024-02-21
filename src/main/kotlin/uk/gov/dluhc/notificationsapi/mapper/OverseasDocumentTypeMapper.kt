package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.OverseasDocumentTypeDto
import uk.gov.dluhc.notificationsapi.models.OverseasDocumentType

@Mapper
interface OverseasDocumentTypeMapper {

    @Mapping(source = "PARENT_MINUS_GUARDIAN", target = "PARENT_GUARDIAN")
    @Mapping(source = "QUALIFYING_MINUS_ADDRESS", target = "QUALIFYING_ADDRESS")
    fun fromApiToDto(overseasDocumentType: OverseasDocumentType): OverseasDocumentTypeDto
}