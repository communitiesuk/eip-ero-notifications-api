package uk.gov.dluhc.notificationsapi.dto

class GenerateRejectedParentGuardianTemplatePreviewDto(
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: RejectedParentGuardianPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.REJECTED_PARENT_GUARDIAN
)
