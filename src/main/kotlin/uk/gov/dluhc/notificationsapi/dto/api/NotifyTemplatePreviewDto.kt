package uk.gov.dluhc.notificationsapi.dto.api

data class NotifyTemplatePreviewDto(
    val text: String,
    val subject: String?,
    val html: String?,
)
