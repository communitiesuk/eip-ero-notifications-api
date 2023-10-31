package uk.gov.dluhc.notificationsapi.dto

class NinoNotMatchedTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    notificationType: NotificationType,
    val personalisation: NinoNotMatchedPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
