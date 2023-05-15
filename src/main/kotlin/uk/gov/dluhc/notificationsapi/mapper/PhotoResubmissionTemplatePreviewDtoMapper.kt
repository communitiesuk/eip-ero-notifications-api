package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class PhotoResubmissionTemplatePreviewDtoMapper {

    @Mapping(target = "notificationType", expression = "java( photoResubmissionNotificationType(request) )")
    abstract fun toPhotoResubmissionTemplatePreviewDto(
        request: GeneratePhotoResubmissionTemplatePreviewRequest
    ): GeneratePhotoResubmissionTemplatePreviewDto

    protected fun photoResubmissionNotificationType(request: GeneratePhotoResubmissionTemplatePreviewRequest): NotificationType =
        if (request.personalisation.photoRejectionReasons.isEmpty())
            NotificationType.PHOTO_RESUBMISSION
        else
            NotificationType.PHOTO_RESUBMISSION_WITH_REASONS
}
