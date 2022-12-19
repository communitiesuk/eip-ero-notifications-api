package uk.gov.dluhc.notificationsapi.dto

data class GeneratePhotoResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val personalisation: PhotoResubmissionPersonalisationDto
)

data class PhotoResubmissionPersonalisationDto(
    val applicationReference: String,
    val firstName: String,
    val photoRequestFreeText: String,
    val uploadPhotoLink: String,
    val eroContactDetails: ContactDetailsDto
)
