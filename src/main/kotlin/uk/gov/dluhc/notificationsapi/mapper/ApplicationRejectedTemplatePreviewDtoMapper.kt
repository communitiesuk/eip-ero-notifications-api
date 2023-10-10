package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationRejectedTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class ApplicationRejectedTemplatePreviewDtoMapper {

    @Autowired
    lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, applicationRejectedTemplatePreviewRequest.getPersonalisation() ) )"
    )
    abstract fun toApplicationRejectedTemplatePreviewDto(applicationRejectedTemplatePreviewRequest: GenerateApplicationRejectedTemplatePreviewRequest): ApplicationRejectedTemplatePreviewDto

    @Mapping(
        target = "rejectionReasonList",
        expression = "java( mapApplicationRejectionReasons( languageDto, personalisation ) )"
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: ApplicationRejectedPersonalisation
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
