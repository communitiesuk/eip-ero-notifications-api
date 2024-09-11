package uk.gov.dluhc.notificationsapi.dto

class NotRegisteredToVoteTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    notificationType: NotificationType,
    val personalisation: NotRegisteredToVotePersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
