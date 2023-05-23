package uk.gov.dluhc.notificationsapi.dto

class GenerateIdDocumentResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: IdDocumentPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.ID_DOCUMENT_RESUBMISSION
)

class GeneratePhotoResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: PhotoPersonalisationDto,
    notificationType: NotificationType
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType
)
