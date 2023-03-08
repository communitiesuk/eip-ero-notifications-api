package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationReceivedTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
interface ApplicationReceivedTemplatePreviewDtoMapper {

    fun toApplicationReceivedTemplatePreviewDto(
        request: GenerateApplicationReceivedTemplatePreviewRequest
    ): ApplicationReceivedTemplatePreviewDto
}
