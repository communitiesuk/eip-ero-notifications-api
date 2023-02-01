package uk.gov.dluhc.notificationsapi.dto

class ApplicationRejectedTemplatePreviewDto(
    language: LanguageDto,
    val personalisation: ApplicationRejectedPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    channel = NotificationChannel.LETTER,
    language = language,
    notificationType = NotificationType.APPLICATION_REJECTED
)
