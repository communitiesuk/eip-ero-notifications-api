package uk.gov.dluhc.notificationsapi.testsupport.model

import java.util.UUID

/**
 * Based on uk.gov.service.notify.TemplatePreview constructor JSON string parsing.
 */
data class NotifyGenerateTemplatePreviewSuccessResponse(
    val id: String = UUID.randomUUID().toString(),
    val type: String = "email",
    val version: Int = 3,
    val body: String = "Hi John",
    val subject: String? = "Photo resubmission",
    val html: String? = "<p style=\"Margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0B0C0C;\">Hi John</p>"
)
