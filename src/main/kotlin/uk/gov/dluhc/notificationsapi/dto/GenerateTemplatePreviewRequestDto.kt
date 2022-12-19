package uk.gov.dluhc.notificationsapi.dto

@Deprecated(message = "Use template specific method")
data class GenerateTemplatePreviewRequestDto(val templateId: String, val personalisation: Map<String, String>)
