package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationReceivedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class ApplicationReceivedTemplatePreviewDtoMapper {

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )",
    )
    abstract fun toApplicationReceivedTemplatePreviewDto(
        request: GenerateApplicationReceivedTemplatePreviewRequest,
    ): ApplicationReceivedTemplatePreviewDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( sourceTypeMapper.toSourceTypeString( sourceType, languageDto ) )",
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: BasePersonalisation,
        sourceType: SourceType,
    ): ApplicationReceivedPersonalisationDto
}
