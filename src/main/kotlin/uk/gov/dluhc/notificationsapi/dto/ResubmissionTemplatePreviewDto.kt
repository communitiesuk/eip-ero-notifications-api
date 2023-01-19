package uk.gov.dluhc.notificationsapi.dto

abstract class BaseGenerateResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val notificationType: NotificationType
)

class GenerateIdDocumentResubmissionTemplatePreviewDto(
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: IdDocumentPersonalisationDto
) : BaseGenerateResubmissionTemplatePreviewDto(
    channel = channel,
    language = language,
    notificationType = NotificationType.ID_DOCUMENT_RESUBMISSION
)

class GeneratePhotoResubmissionTemplatePreviewDto(
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: PhotoPersonalisationDto
) : BaseGenerateResubmissionTemplatePreviewDto(
    channel = channel,
    language = language,
    notificationType = NotificationType.PHOTO_RESUBMISSION
)
