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

class SignatureResubmissionPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val personalisationSourceTypeString: String,
    val rejectionNotes: String?,
    val rejectionReasons: List<String>,
    val rejectionFreeText: String?,
    val deadline: String?,
    val signatureNotSuitableText: String?,
    val uploadSignatureLink: String,
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
    val personalisationSourceTypeString: String,
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
    val personalisationSourceTypeString: String,
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
    val personalisationSourceTypeString: String,
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
    val personalisationSourceTypeString: String,
    val freeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RequiredDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val personalisationSourceTypeString: String,
    val additionalNotes: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class BespokeCommPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val personalisationFullSourceTypeString: String,
    val subjectHeader: String,
    val details: String,
    val whatToDo: String?,
    val deadline: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class NotRegisteredToVotePersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val personalisationFullSourceTypeString: String,
    val freeText: String?,
    val property: String?,
    val street: String?,
    val town: String?,
    val area: String?,
    val locality: String?,
    val postcode: String?,
    val deadline: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RejectedOverseasDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val documents: List<String>,
    val rejectedDocumentFreeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)

class RequiredOverseasDocumentPersonalisationDto(
    applicationReference: String,
    firstName: String,
    eroContactDetails: ContactDetailsDto,
    val requiredDocumentFreeText: String?,
) : BaseTemplatePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
)
