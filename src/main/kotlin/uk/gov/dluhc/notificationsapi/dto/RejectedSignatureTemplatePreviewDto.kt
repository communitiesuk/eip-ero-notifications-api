package uk.gov.dluhc.notificationsapi.dto

class RejectedSignatureTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: RejectedSignaturePersonalisationDto,
    notificationType: NotificationType
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType
)
