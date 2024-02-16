package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateParentGuardianRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.api.GenerateQualifyingAddressRequiredTemplatePreviewDto
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
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateIdDocumentResubmissionTemplatePreview(request: GenerateIdDocumentResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateIdDocumentRequiredTemplatePreview(request: GenerateIdDocumentRequiredTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toIdDocumentRequiredTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateApplicationReceivedTemplatePreview(request: ApplicationReceivedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateApplicationApprovedTemplatePreview(request: GenerateApplicationApprovedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateApplicationRejectedTemplatePreview(dto: ApplicationRejectedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toApplicationRejectedTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateRejectedDocumentTemplatePreview(dto: RejectedDocumentTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toRejectedDocumentTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateRejectedSignatureTemplatePreview(dto: RejectedSignatureTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toRejectedSignatureTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateRequestedSignatureTemplatePreview(dto: RequestedSignatureTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toRequestedSignatureTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateNinoNotMatchedTemplatePreview(dto: NinoNotMatchedTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toNinoNotMatchedTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateParentGuardianRequiredTemplatePreview(dto: GenerateParentGuardianRequiredTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toParentGuardianRequiredTemplatePersonalisationMap(personalisation)
            )
        }
    }

    fun generateQualifyingAddressRequiredTemplatePreview(dto: GenerateQualifyingAddressRequiredTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language
                ),
                templatePersonalisationDtoMapper.toQualifyingAddressRequiredTemplatePersonalisationMap(personalisation)
            )
        }
    }
}
