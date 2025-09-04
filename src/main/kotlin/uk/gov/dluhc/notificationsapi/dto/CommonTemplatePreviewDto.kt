package uk.gov.dluhc.notificationsapi.dto

data class CommonTemplatePreviewDto(
    val channel: CommunicationChannel,
    val language: LanguageDto,
    val sourceType: SourceType,
    val notificationType: NotificationType,
)
