package uk.gov.dluhc.notificationsapi.dto

import java.util.UUID

data class SendNotificationResponseDto(
    val notificationId: UUID,
    val reference: String,
    val templateId: UUID,
    val templateVersion: Int,
    val templateUri: String,
    val body: String,
    val subject: String,
    var fromEmail: String?,
)
