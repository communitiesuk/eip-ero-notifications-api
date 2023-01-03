package uk.gov.dluhc.notificationsapi.dto

abstract class BaseGenerateResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
)

class GenerateIdDocumentResubmissionTemplatePreviewDto(
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: IdDocumentPersonalisationDto
) : BaseGenerateResubmissionTemplatePreviewDto(
    channel = channel,
    language = language,
)

class GeneratePhotoResubmissionTemplatePreviewDto(
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: PhotoPersonalisationDto
) : BaseGenerateResubmissionTemplatePreviewDto(
    channel = channel,
    language = language,
)
