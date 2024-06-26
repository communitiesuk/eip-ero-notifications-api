package uk.gov.dluhc.notificationsapi.dto

class GenerateIdDocumentRequiredTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: IdDocumentRequiredPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.ID_DOCUMENT_REQUIRED,
)
