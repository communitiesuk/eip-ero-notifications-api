package uk.gov.dluhc.notificationsapi.domain

data class SendNotificationRequest(
    val gssCode: String,
    val requestor: String,
    val sourceType: SourceType,
    val sourceReference: String,
    val emailAddress: String,
    val notificationType: NotificationType,
    val personalisation: Map<String, String>
)
