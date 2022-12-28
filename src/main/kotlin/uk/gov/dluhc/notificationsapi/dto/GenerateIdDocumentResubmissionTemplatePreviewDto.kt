package uk.gov.dluhc.notificationsapi.dto

data class GenerateIdDocumentResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val personalisation: PhotoResubmissionPersonalisationDto
)
