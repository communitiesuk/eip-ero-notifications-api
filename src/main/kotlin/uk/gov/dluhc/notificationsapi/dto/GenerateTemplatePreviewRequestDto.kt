package uk.gov.dluhc.notificationsapi.dto

data class GenerateTemplatePreviewRequestDto(val templateId: String, val personalisation: Map<String, String>)
