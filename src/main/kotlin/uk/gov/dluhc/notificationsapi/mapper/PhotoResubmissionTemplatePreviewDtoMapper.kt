package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.PhotoPersonalisation

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class PhotoResubmissionTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Mapping(target = "notificationType", expression = "java( photoResubmissionNotificationType(request) )")
    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation() ) )"
    )
    abstract fun toPhotoResubmissionTemplatePreviewDto(
        request: GeneratePhotoResubmissionTemplatePreviewRequest
    ): GeneratePhotoResubmissionTemplatePreviewDto

    protected fun photoResubmissionNotificationType(request: GeneratePhotoResubmissionTemplatePreviewRequest): NotificationType =
        if (request.personalisation.photoRejectionReasons.isEmpty())
            NotificationType.PHOTO_RESUBMISSION
        else
            NotificationType.PHOTO_RESUBMISSION_WITH_REASONS

    @Mapping(
        target = "photoRejectionReasons",
        expression = "java( mapPhotoRejectionReasons( languageDto, personalisation ) )"
    )
    protected abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: PhotoPersonalisation
    ): PhotoPersonalisationDto

    protected fun mapPhotoRejectionReasons(
        languageDto: LanguageDto,
        personalisation: PhotoPersonalisation
    ): List<String> {
        return personalisation.photoRejectionReasons.map { reason ->
            photoRejectionReasonMapper.toPhotoRejectionReasonString(
                reason,
                languageDto
            )
        }
    }
}
