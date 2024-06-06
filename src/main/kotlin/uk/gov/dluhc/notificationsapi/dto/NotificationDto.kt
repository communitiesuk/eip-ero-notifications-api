package uk.gov.dluhc.notificationsapi.dto

import java.time.LocalDateTime
import java.util.UUID

data class NotificationDto(
    var id: UUID,
    var sourceReference: String,
    var gssCode: String,
    var sourceType: SourceType,
    var type: NotificationType,
    var channel: NotificationChannel,
    var toEmail: String,
    var toPostalAddress: PostalAddress,
    var requestor: String,
    var sentAt: LocalDateTime,
    var personalisation: Map<String, Any>,
    var notifyDetails: NotifyDetails,
)
