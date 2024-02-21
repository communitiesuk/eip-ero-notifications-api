package uk.gov.dluhc.notificationsapi.dto

class GenerateQualifyingAddressRequiredTemplatePreviewDto(
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: QualifyingAddressPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.QUALIFYING_ADDRESS_REQUIRED
)
