package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentRequiredTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface GenerateIdDocumentRequiredTemplatePreviewDtoMapper {

    fun toGenerateIdDocumentRequiredTemplatePreviewDto(request: GenerateIdDocumentRequiredTemplatePreviewRequest): GenerateIdDocumentRequiredTemplatePreviewDto
}
