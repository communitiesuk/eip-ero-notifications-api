package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.GenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationApprovedTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
interface GenerateApplicationApprovedTemplatePreviewDtoMapper {

    fun toApplicationApprovedTemplatePreviewDto(
        request: GenerateApplicationApprovedTemplatePreviewRequest,
    ): GenerateApplicationApprovedTemplatePreviewDto
}
