package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface RejectedSignatureTemplatePreviewDtoMapper {
    @Mapping(
        source = "request.personalisation.rejectionReasons",
        target = "personalisation.rejectionReasons",
        defaultExpression = "java(java.util.Collections.emptyList())"
    )
    fun toRejectedSignatureTemplatePreviewDto(request: GenerateRejectedSignatureTemplatePreviewRequest): RejectedSignatureTemplatePreviewDto
}
