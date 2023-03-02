package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDtoEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum

@Mapper
interface SourceTypeMapper {

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    @ValueMapping(source = "POSTAL", target = "POSTAL")
    fun fromMessageToDto(sourceType: SourceTypeMessageEnum): SourceTypeDtoEnum

    fun fromDtoToEntity(sourceType: SourceTypeDtoEnum): SourceTypeEntityEnum

    fun fromEntityToDto(sourceType: SourceTypeEntityEnum): SourceTypeDtoEnum
}
