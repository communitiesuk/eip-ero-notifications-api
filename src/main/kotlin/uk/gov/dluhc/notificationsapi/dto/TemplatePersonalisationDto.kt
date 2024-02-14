package uk.gov.dluhc.notificationsapi.dto

abstract class BaseTemplatePersonalisationDto(
    val applicationReference: String,
    val firstName: String,
    val eroContactDetails: ContactDetailsDto,
)

class IdDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val idDocumentRequestFreeText: String,
    val documentRejectionText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class IdDocumentRequiredPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val idDocumentRequiredFreeText: String,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class PhotoPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val photoRejectionReasons: List<String>,
    val photoRejectionNotes: String?,
    val photoRequestFreeText: String,
    val uploadPhotoLink: String,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class ApplicationApprovedPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class ApplicationReceivedPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val sourceType: String,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class ApplicationRejectedPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val rejectionReasonList: List<String>,
    val rejectionReasonMessage: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RejectedDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val sourceType: String,
    val documents: List<String>,
    val rejectedDocumentFreeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RejectedSignaturePersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val sourceType: String,
    val rejectionNotes: String?,
    val rejectionReasons: List<String>,
    val rejectionFreeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RequestedSignaturePersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val sourceType: String,
    val freeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class NinoNotMatchedPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val sourceType: String,
    val additionalNotes: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class ParentGuardianPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val freeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)
