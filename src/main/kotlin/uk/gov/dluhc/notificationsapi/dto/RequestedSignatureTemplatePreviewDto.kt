package uk.gov.dluhc.notificationsapi.dto

class RequestedSignatureTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: RequestedSignaturePersonalisationDto,
    notificationType: NotificationType,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
