package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface IdentityDocumentResubmissionTemplatePreviewDtoMapper {

    fun toIdDocumentResubmissionTemplatePreviewDto(
        request: GenerateIdDocumentResubmissionTemplatePreviewRequest
    ): GenerateIdDocumentResubmissionTemplatePreviewDto
}
