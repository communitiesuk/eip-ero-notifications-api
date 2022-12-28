package uk.gov.dluhc.notificationsapi.dto

data class GenerateIdDocumentResubmissionTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val personalisation: IdDocumentResubmissionPersonalisationDto
)

data class IdDocumentResubmissionPersonalisationDto(
    val applicationReference: String,
    val firstName: String,
    val idDocumentRequestFreeText: String,
    val eroContactDetails: ContactDetailsDto
)
