package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateApplicationApprovedTemplatePreviewDto
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
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(notificationType, channel, language),
                templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateIdDocumentResubmissionTemplatePreview(request: GenerateIdDocumentResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(notificationType, channel, language),
                templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateApplicationApprovedTemplatePreview(request: GenerateApplicationApprovedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(notificationType, channel, language),
                templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateApplicationRejectedTemplatePreview(dto: ApplicationRejectedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(notificationType, channel, language),
                templatePersonalisationDtoMapper.toApplicationRejectedTemplatePersonalisationMap(personalisation)
            )
        }
    }
}
