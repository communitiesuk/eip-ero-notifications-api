package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommonTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GenerateRequiredOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVoteTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.mapper.DocumentCategoryMapper
import uk.gov.dluhc.notificationsapi.mapper.SignatureResubmissionTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse

@Service
class TemplateService(
    private val govNotifyApiClient: GovNotifyApiClient,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
    private val notificationTemplateMapper: NotificationTemplateMapper,
    private val documentCategoryMapper: DocumentCategoryMapper,
    private val commonTemplateService: CommonTemplateService,
    private val signatureResubmissionPreviewDtoMapper: SignatureResubmissionTemplatePreviewDtoMapper,
) {

    fun generatePhotoResubmissionTemplatePreview(request: GeneratePhotoResubmissionTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(request) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language,
                ),
                templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toIdDocumentRequiredTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toApplicationRejectedTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toRejectedDocumentTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toRejectedSignatureTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toRequestedSignatureTemplatePersonalisationMap(personalisation),
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
                    language,
                ),
                templatePersonalisationDtoMapper.toRequiredDocumentTemplatePersonalisationMap(
                    personalisation,
                    sourceType,
                ),
            )
        }
    }

    fun generateBespokeCommTemplatePreview(dto: BespokeCommTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language,
                ),
                templatePersonalisationDtoMapper.toBespokeCommTemplatePersonalisationMap(
                    personalisation,
                    language,
                ),
            )
        }
    }

    fun generateNotRegisteredToVoteTemplatePreview(dto: NotRegisteredToVoteTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    channel,
                    language,
                ),
                templatePersonalisationDtoMapper.toNotRegisteredToVoteTemplatePersonalisationMap(
                    personalisation,
                    language,
                ),
            )
        }
    }

    fun generateRejectedOverseasDocumentTemplatePreview(dto: GenerateRejectedOverseasDocumentTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType = SourceType.OVERSEAS,
                    notificationType = documentCategoryMapper.fromRejectedDocumentCategoryDtoToNotificationTypeDto(
                        documentCategory,
                    ),
                    channel,
                    language,
                ),
                templatePersonalisationDtoMapper.toRejectedOverseasDocumentTemplatePersonalisationMap(personalisation),
            )
        }
    }

    fun generateRequiredOverseasDocumentTemplatePreview(dto: GenerateRequiredOverseasDocumentTemplatePreviewDto): NotifyTemplatePreviewDto {
        return with(dto) {
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    sourceType = SourceType.OVERSEAS,
                    notificationType = documentCategoryMapper.fromRequiredDocumentCategoryDtoToNotificationTypeDto(
                        documentCategory,
                    ),
                    channel,
                    language,
                ),
                templatePersonalisationDtoMapper.toRequiredOverseasDocumentTemplatePersonalisationMap(personalisation),
            )
        }
    }

    fun generateSignatureResubmissionTemplatePreview(request: GenerateSignatureResubmissionTemplatePreviewRequest): GenerateTemplatePreviewResponse {
        val notificationTypeDto = signatureResubmissionPreviewDtoMapper.signatureResubmissionNotificationType(request)
        val getPersonalisation = { commonTemplatePreviewDto: CommonTemplatePreviewDto ->
            signatureResubmissionPreviewDtoMapper.toSignatureResubmissionPersonalisation(request, commonTemplatePreviewDto)
        }

        return commonTemplateService.generateTemplatePreview(
            request.channel,
            request.sourceType,
            request.language,
            notificationTypeDto,
            getPersonalisation,
        )
    }
}
