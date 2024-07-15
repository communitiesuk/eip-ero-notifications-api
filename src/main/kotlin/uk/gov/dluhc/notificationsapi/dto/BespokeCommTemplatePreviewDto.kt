package uk.gov.dluhc.notificationsapi.dto

class BespokeCommTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    notificationType: NotificationType,
    val personalisation: BespokeCommPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
