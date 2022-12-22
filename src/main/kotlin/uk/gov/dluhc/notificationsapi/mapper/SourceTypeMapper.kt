package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum

@Mapper
interface SourceTypeMapper {

    @ValueMapping(target = "VOTER_CARD", source = "VOTER_MINUS_CARD")
    fun toSourceTypeDto(sourceType: SourceTypeMessageEnum): SourceType

    fun toSourceTypeEntity(sourceType: SourceType): SourceTypeEntityEnum
}
