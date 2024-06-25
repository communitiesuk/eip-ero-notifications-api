package uk.gov.dluhc.notificationsapi.dto

abstract class BaseGenerateTemplatePreviewDto(
    val channel: CommunicationChannel,
    val language: LanguageDto,
    val notificationType: NotificationType,
    val sourceType: SourceType,
)
