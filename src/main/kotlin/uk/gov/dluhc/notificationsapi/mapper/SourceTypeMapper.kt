package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDtoEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApiEnum

@Mapper
abstract class SourceTypeMapper {

    @Autowired
    private lateinit var messageSource: MessageSource

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    abstract fun fromMessageToDto(sourceType: SourceTypeMessageEnum): SourceTypeDtoEnum

    abstract fun fromDtoToEntity(sourceType: SourceTypeDtoEnum): SourceTypeEntityEnum

    @InheritInverseConfiguration
    abstract fun fromEntityToDto(sourceType: SourceTypeEntityEnum): SourceTypeDtoEnum

    @ValueMapping(source = "VOTER_MINUS_CARD", target = "VOTER_CARD")
    abstract fun fromApiToDto(sourceType: SourceTypeApiEnum): SourceTypeDtoEnum

    fun toSourceTypeString(
        sourceType: SourceTypeApiEnum,
        languageDto: LanguageDto,
    ): String {
        return messageSource.getMessage(
            "templates.vote-type.${sourceType.value}",
            null,
            languageDto.locale
        )
    }

    fun toSourceTypeString(
        sourceType: SourceTypeMessageEnum,
        languageDto: LanguageDto,
    ): String {
        return messageSource.getMessage(
            "templates.vote-type.${sourceType.value}",
            null,
            languageDto.locale
        )
    }
}
