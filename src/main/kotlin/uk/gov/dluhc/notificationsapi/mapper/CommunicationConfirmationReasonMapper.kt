package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationReasonDto
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason as OfflineCommunicationReasonEntity
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason as OfflineCommunicationReasonApi

@Mapper
interface CommunicationConfirmationReasonMapper {

    @ValueMapping(target = "APPLICATION_REJECTED", source = "APPLICATION_MINUS_REJECTED")
    @ValueMapping(target = "PHOTO_REJECTED", source = "PHOTO_MINUS_REJECTED")
    @ValueMapping(target = "DOCUMENT_REJECTED", source = "DOCUMENT_MINUS_REJECTED")
    fun fromApiToDto(apiReasonEnum: OfflineCommunicationReasonApi): CommunicationConfirmationReasonDto

    @InheritInverseConfiguration
    fun fromDtoToApi(dtoReasonEnum: CommunicationConfirmationReasonDto): OfflineCommunicationReasonApi

    fun fromDtoToEntity(dtoEnum: CommunicationConfirmationReasonDto): OfflineCommunicationReasonEntity

    fun fromEntityToDto(entityEnum: OfflineCommunicationReasonEntity): CommunicationConfirmationReasonDto
}
