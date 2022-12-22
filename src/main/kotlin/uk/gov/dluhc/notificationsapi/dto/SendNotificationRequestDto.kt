package uk.gov.dluhc.notificationsapi.dto

data class SendNotificationRequestDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val gssCode: String,
    val requestor: String,
    val sourceType: SourceType,
    val sourceReference: String,
    val emailAddress: String,
    val notificationType: NotificationType,
)
