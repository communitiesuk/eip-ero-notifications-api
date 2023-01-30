package uk.gov.dluhc.notificationsapi.dto

abstract class BaseTemplatePersonalisationDto(
    val applicationReference: String,
    val firstName: String,
    val eroContactDetails: ContactDetailsDto
)

class IdDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val idDocumentRequestFreeText: String
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails
)

class PhotoPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val photoRequestFreeText: String,
    val uploadPhotoLink: String
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails
)

class ApplicationApprovedPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails
)
