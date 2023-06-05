package uk.gov.dluhc.notificationsapi.dto

class NinoNotMatchedTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: NinoNotMatchedPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.NINO_NOT_MATCHED
)
