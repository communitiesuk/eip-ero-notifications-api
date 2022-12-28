package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionPersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.models.TemplateType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION
import uk.gov.dluhc.notificationsapi.models.TemplateType.PHOTO_MINUS_RESUBMISSION

@Service
class TemplateService(
    private val govNotifyApiClient: GovNotifyApiClient,
    private val photoResubmissionPersonalisationDtoMapper: PhotoResubmissionPersonalisationDtoMapper,
    private val notificationTemplateMapper: NotificationTemplateMapper
) {

    fun generatePhotoResubmissionTemplatePreview(request: GeneratePhotoResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(PHOTO_MINUS_RESUBMISSION, channel, language),
                photoResubmissionPersonalisationDtoMapper.toTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateIdDocumentResubmissionTemplatePreview(request: GenerateIdDocumentResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(ID_MINUS_DOCUMENT_MINUS_RESUBMISSION, channel, language),
                photoResubmissionPersonalisationDtoMapper.toIdDocumentTemplatePersonalisationMap(personalisation)
            )
        }
    }
}
