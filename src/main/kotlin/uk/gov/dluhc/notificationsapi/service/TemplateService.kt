package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper

@Service
class TemplateService(
    private val govNotifyApiClient: GovNotifyApiClient,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
    private val notificationTemplateMapper: NotificationTemplateMapper
) {

    fun generatePhotoResubmissionTemplatePreview(request: GeneratePhotoResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(templateType, channel, language),
                templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateIdDocumentResubmissionTemplatePreview(request: GenerateIdDocumentResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromTemplateTypeForChannelAndLanguage(templateType, channel, language),
                templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }
}
