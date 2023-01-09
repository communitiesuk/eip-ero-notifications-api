package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class])
interface ResubmissionTemplatePreviewDtoMapper {

    fun toPhotoResubmissionTemplatePreviewDto(
        request: GeneratePhotoResubmissionTemplatePreviewRequest
    ): GeneratePhotoResubmissionTemplatePreviewDto

    fun toIdDocumentResubmissionTemplatePreviewDto(
        request: GenerateIdDocumentResubmissionTemplatePreviewRequest
    ): GenerateIdDocumentResubmissionTemplatePreviewDto
}
