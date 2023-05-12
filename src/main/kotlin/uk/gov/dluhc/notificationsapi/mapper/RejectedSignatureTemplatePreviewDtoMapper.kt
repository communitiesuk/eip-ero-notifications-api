package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface RejectedSignatureTemplatePreviewDtoMapper {
    fun toRejectedSignatureTemplatePreviewDto(request: GenerateRejectedSignatureTemplatePreviewRequest): RejectedSignatureTemplatePreviewDto
}
