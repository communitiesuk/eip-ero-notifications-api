package uk.gov.dluhc.notificationsapi.dto

class InviteToRegisterTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    notificationType: NotificationType,
    val personalisation: InviteToRegisterPersonalisationDto,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
