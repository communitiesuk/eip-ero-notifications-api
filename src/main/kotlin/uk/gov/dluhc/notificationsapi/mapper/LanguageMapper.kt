package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language

@Mapper
interface LanguageMapper {

    @ValueMapping(target = "WELSH", source = "CY")
    @ValueMapping(target = "ENGLISH", source = "EN")
    fun toDto(apiLanguageEnum: Language): LanguageDto
}
