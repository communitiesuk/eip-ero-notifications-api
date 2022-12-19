package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionPersonalisationMapper
import uk.gov.dluhc.notificationsapi.models.TemplateType

@Service
class TemplateService(
    private val govNotifyApiClient: GovNotifyApiClient,
    private val photoResubmissionPersonalisationMapper: PhotoResubmissionPersonalisationMapper,
    private val notificationTemplateMapper: NotificationTemplateMapper
) {

    @Deprecated(message = "Use template specific method")
    fun generateTemplatePreview(request: GenerateTemplatePreviewRequestDto): NotifyTemplatePreviewDto =
        with(request) { govNotifyApiClient.generateTemplatePreview(templateId, personalisation) }

    fun generatePhotoResubmissionTemplatePreview(request: GeneratePhotoResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromTemplateType(TemplateType.PHOTO_MINUS_RESUBMISSION),
                photoResubmissionPersonalisationMapper.toTemplatePersonalisationMap(personalisation)
            )
        }
    }
}
