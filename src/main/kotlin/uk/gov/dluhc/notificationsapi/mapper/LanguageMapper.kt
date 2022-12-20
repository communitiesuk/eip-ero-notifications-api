package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language

@Mapper
interface LanguageMapper {

    fun toDto(modelLanguage: Language): LanguageDto
}
