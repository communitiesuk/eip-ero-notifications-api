package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDtoEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApiEnum

@Component
class SourceTypeMapper(private val messageSource: MessageSource) {

    fun fromMessageToDto(sourceType: SourceTypeMessageEnum) = when (sourceType) {
        SourceType.VOTER_MINUS_CARD -> SourceTypeDtoEnum.VOTER_CARD
        SourceType.POSTAL -> SourceTypeDtoEnum.POSTAL
        SourceType.PROXY -> SourceTypeDtoEnum.PROXY
        SourceType.OVERSEAS -> SourceTypeDtoEnum.OVERSEAS
    }

    fun fromDtoToEntity(sourceType: SourceTypeDtoEnum) = when (sourceType) {
        SourceTypeDtoEnum.VOTER_CARD -> SourceTypeEntityEnum.VOTER_CARD
        SourceTypeDtoEnum.POSTAL -> SourceTypeEntityEnum.POSTAL
        SourceTypeDtoEnum.ANONYMOUS_ELECTOR_DOCUMENT -> SourceTypeEntityEnum.ANONYMOUS_ELECTOR_DOCUMENT
        SourceTypeDtoEnum.PROXY -> SourceTypeEntityEnum.PROXY
        SourceTypeDtoEnum.OVERSEAS -> SourceTypeEntityEnum.OVERSEAS
    }

    fun fromEntityToDto(sourceType: SourceTypeEntityEnum) = when (sourceType) {
        SourceTypeEntityEnum.VOTER_CARD -> SourceTypeDtoEnum.VOTER_CARD
        SourceTypeEntityEnum.POSTAL -> SourceTypeDtoEnum.POSTAL
        SourceTypeEntityEnum.ANONYMOUS_ELECTOR_DOCUMENT -> SourceTypeDtoEnum.ANONYMOUS_ELECTOR_DOCUMENT
        SourceTypeEntityEnum.PROXY -> SourceTypeDtoEnum.PROXY
        SourceTypeEntityEnum.OVERSEAS -> SourceTypeDtoEnum.OVERSEAS
    }

    fun fromApiToDto(sourceType: SourceTypeApiEnum) = when (sourceType) {
        SourceTypeApiEnum.VOTER_MINUS_CARD -> SourceTypeDtoEnum.VOTER_CARD
        SourceTypeApiEnum.POSTAL -> SourceTypeDtoEnum.POSTAL
        SourceTypeApiEnum.PROXY -> SourceTypeDtoEnum.PROXY
        SourceTypeApiEnum.OVERSEAS -> SourceTypeDtoEnum.OVERSEAS
    }

    fun toSourceTypeString(
        sourceType: SourceTypeApiEnum,
        languageDto: LanguageDto,
    ): String {
        return messageSource.getMessage(
            "templates.vote-type.${sourceType.value}",
            null,
            languageDto.locale,
        )
    }

    fun toSourceTypeString(
        sourceType: SourceTypeMessageEnum,
        languageDto: LanguageDto,
    ): String {
        return messageSource.getMessage(
            "templates.vote-type.${sourceType.value}",
            null,
            languageDto.locale,
        )
    }
}
