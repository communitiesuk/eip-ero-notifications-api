package uk.gov.dluhc.notificationsapi.dto

import java.util.UUID

data class NotifyDetailsDto(
    var notificationId: UUID,
    var reference: String,
    var templateId: UUID,
    var templateVersion: Int,
    var templateUri: String,
    var body: String,
    var subject: String,
    var fromEmail: String?,
)
