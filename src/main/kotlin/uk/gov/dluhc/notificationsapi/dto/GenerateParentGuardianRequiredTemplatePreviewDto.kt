package uk.gov.dluhc.notificationsapi.dto

class GenerateParentGuardianRequiredTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: ParentGuardianPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.PARENT_GUARDIAN_REQUIRED
)