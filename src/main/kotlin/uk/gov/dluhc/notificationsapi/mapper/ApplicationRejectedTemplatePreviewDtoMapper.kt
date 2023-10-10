package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationRejectedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class ApplicationRejectedTemplatePreviewDtoMapper {

    @Autowired
    lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )"
    )
    abstract fun toApplicationRejectedTemplatePreviewDto(request: GenerateApplicationRejectedTemplatePreviewRequest): ApplicationRejectedTemplatePreviewDto

    @Mapping(
        target = "sourceType",
        expression = "java( sourceTypeMapper.toSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        target = "rejectionReasonList",
        expression = "java( mapApplicationRejectionReasons( languageDto, personalisation ) )"
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: ApplicationRejectedPersonalisation,
        sourceType: SourceType,
    ): ApplicationRejectedPersonalisationDto

    fun mapApplicationRejectionReasons(
        languageDto: LanguageDto,
        personalisation: ApplicationRejectedPersonalisation
    ): List<String> {
        return personalisation.rejectionReasonList.map { reason ->
            applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                reason,
                languageDto
            )
        }
    }
}
