package uk.gov.dluhc.notificationsapi.dto

class RejectedDocumentTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: RejectedDocumentPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.REJECTED_DOCUMENT,
)
