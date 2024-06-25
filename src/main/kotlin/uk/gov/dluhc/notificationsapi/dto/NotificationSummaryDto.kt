package uk.gov.dluhc.notificationsapi.dto

import java.time.LocalDateTime
import java.util.UUID

data class NotificationSummaryDto(
    val id: UUID,
    var sourceReference: String,
    val sourceType: SourceType,
    val gssCode: String,
    var type: NotificationType,
    var channel: CommunicationChannel,
    var requestor: String,
    var sentAt: LocalDateTime,
)
