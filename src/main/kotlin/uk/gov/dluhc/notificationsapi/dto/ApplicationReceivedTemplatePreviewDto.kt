package uk.gov.dluhc.notificationsapi.dto

class ApplicationReceivedTemplatePreviewDto(
    val personalisation: ApplicationReceivedPersonalisationDto,
    language: LanguageDto,
    sourceType: SourceType,
) : BaseGenerateTemplatePreviewDto(
    channel = CommunicationChannel.EMAIL,
    language = language,
    notificationType = NotificationType.APPLICATION_RECEIVED,
    sourceType = sourceType,
)
