package uk.gov.dluhc.notificationsapi.dto

class ApplicationRejectedTemplatePreviewDto(
    sourceType: SourceType,
    language: LanguageDto,
    val personalisation: ApplicationRejectedPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = NotificationChannel.LETTER,
    language = language,
    notificationType = NotificationType.APPLICATION_REJECTED,
)
