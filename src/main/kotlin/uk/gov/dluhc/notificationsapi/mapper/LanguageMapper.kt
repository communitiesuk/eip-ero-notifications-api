package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.Language as MessageLanguageEnum
@Mapper
interface LanguageMapper {

    @ValueMapping(target = "WELSH", source = "CY")
    @ValueMapping(target = "ENGLISH", source = "EN")
    fun fromApiToDto(apiLanguageEnum: Language): LanguageDto

    @ValueMapping(target = "WELSH", source = "CY")
    @ValueMapping(target = "ENGLISH", source = "EN")
    fun fromMessageToDto(messageLanguageEnum: MessageLanguageEnum): LanguageDto
}
