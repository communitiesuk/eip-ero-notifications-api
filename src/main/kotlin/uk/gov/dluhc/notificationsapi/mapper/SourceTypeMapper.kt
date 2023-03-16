package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDtoEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApiEnum

@Mapper
interface SourceTypeMapper {

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    @ValueMapping(source = "POSTAL", target = "POSTAL")
    @ValueMapping(source = "PROXY", target = "PROXY")
    fun fromMessageToDto(sourceType: SourceTypeMessageEnum): SourceTypeDtoEnum

    fun fromDtoToEntity(sourceType: SourceTypeDtoEnum): SourceTypeEntityEnum

    @InheritInverseConfiguration
    fun fromEntityToDto(sourceType: SourceTypeEntityEnum): SourceTypeDtoEnum

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    @ValueMapping(source = "POSTAL", target = "POSTAL")
    @ValueMapping(source = "PROXY", target = "PROXY")
    fun fromApiToDto(sourceType: SourceTypeApiEnum): SourceTypeDtoEnum
}
