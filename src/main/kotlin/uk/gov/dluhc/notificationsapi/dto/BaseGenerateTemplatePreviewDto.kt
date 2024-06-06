package uk.gov.dluhc.notificationsapi.dto

abstract class BaseGenerateTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val notificationType: NotificationType,
    val sourceType: SourceType,
)
