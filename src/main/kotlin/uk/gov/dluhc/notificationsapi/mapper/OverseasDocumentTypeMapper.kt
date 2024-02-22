package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.OverseasDocumentTypeDto
import uk.gov.dluhc.notificationsapi.models.OverseasDocumentType

@Mapper
interface OverseasDocumentTypeMapper {

    @ValueMapping(source = "PARENT_MINUS_GUARDIAN", target = "PARENT_GUARDIAN")
    @ValueMapping(source = "QUALIFYING_MINUS_ADDRESS", target = "QUALIFYING_ADDRESS")
    fun fromApiToDto(overseasDocumentType: OverseasDocumentType): OverseasDocumentTypeDto

    @ValueMapping(source = "IDENTITY", target = "REJECTED_DOCUMENT")
    @ValueMapping(source = "PARENT_GUARDIAN", target = "REJECTED_PARENT_GUARDIAN")
    @ValueMapping(source = "QUALIFYING_ADDRESS", target = "REJECTED_QUALIFYING_ADDRESS")
    fun fromOverseasDocumentTypeDtoToNotificationTypeDto(overseasDocumentType: OverseasDocumentTypeDto): NotificationType
}