package uk.gov.dluhc.notificationsapi.dto

data class PhotoResubmissionPersonalisationDto(
    val applicationReference: String,
    val firstName: String,
    val photoRequestFreeText: String,
    val uploadPhotoLink: String,
    val eroContactDetails: ContactDetailsDto
)
