package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationRejectedTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, ApplicationRejectionReasonMapper::class, SourceTypeMapper::class])
interface ApplicationRejectedTemplatePreviewDtoMapper {

    fun toApplicationRejectedTemplatePreviewDto(applicationRejectedTemplatePreviewRequest: GenerateApplicationRejectedTemplatePreviewRequest): ApplicationRejectedTemplatePreviewDto
}
