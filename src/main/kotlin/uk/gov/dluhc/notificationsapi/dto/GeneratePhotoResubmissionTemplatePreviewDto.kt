package uk.gov.dluhc.notificationsapi.dto

data class GeneratePhotoResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val personalisation: PhotoResubmissionPersonalisationDto
)
