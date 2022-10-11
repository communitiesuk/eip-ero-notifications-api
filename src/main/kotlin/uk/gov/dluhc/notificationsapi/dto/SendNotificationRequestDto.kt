package uk.gov.dluhc.notificationsapi.dto

data class SendNotificationRequestDto(
    val gssCode: String,
    val requestor: String,
    val sourceType: SourceType,
    val sourceReference: String,
    val emailAddress: String,
    val notificationType: NotificationType,
    val channel: NotificationChannel,
    val personalisation: Map<String, String>
)
