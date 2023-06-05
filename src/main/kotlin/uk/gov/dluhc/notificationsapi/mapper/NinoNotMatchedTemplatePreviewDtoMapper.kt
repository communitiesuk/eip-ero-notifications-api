package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateNinoNotMatchedTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface NinoNotMatchedTemplatePreviewDtoMapper {
    @Mapping(
        source = "request.personalisation.additionalNotes",
        target = "personalisation.additionalNotes"
    )
    fun toDto(request: GenerateNinoNotMatchedTemplatePreviewRequest): NinoNotMatchedTemplatePreviewDto
}
