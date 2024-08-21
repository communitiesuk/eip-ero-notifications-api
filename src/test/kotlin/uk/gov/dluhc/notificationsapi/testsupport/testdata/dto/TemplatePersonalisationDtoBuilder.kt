package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildApplicationRejectedPersonalisationDto

fun buildPhotoPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRejectionReasons: List<String> = listOf("There are other people or objects in the photo"),
    photoRejectionNotes: String? = "Please ensure you frame only your head and shoulders in the photo",
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
): PhotoPersonalisationDto =
    PhotoPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRejectionReasons = photoRejectionReasons,
        photoRejectionNotes = photoRejectionNotes,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails,
    )

fun buildIdDocumentPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequestFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    documentRejectionText: String? = null,
): IdDocumentPersonalisationDto =
    IdDocumentPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = idDocumentRequestFreeText,
        eroContactDetails = eroContactDetails,
        documentRejectionText = documentRejectionText,
    )

fun buildIdDocumentRequiredPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    idDocumentRequiredFreeText: String = faker.harryPotter().spell(),
): IdDocumentRequiredPersonalisationDto =
    IdDocumentRequiredPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        idDocumentRequiredFreeText = idDocumentRequiredFreeText,
    )

fun buildApplicationReceivedPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    sourceType: String = "postal",
): ApplicationReceivedPersonalisationDto =
    ApplicationReceivedPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        personalisationSourceTypeString = sourceType,
    )

fun buildApplicationApprovedPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
): ApplicationApprovedPersonalisationDto =
    ApplicationApprovedPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
    )

fun buildRejectedDocumentPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    rejectedDocumentFreeText: String? = faker.harryPotter().spell(),
    documents: List<String> = listOf(faker.lordOfTheRings().location()),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    sourceType: String = "postal",
): RejectedDocumentPersonalisationDto =
    RejectedDocumentPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        documents = documents,
        rejectedDocumentFreeText = rejectedDocumentFreeText,
        eroContactDetails = eroContactDetails,
        personalisationSourceTypeString = sourceType,
    )

fun buildPhotoPersonalisationDtoFromMessage(
    personalisationMessage: PhotoPersonalisation,
    photoRejectionReasons: List<String>,
): PhotoPersonalisationDto {
    return with(personalisationMessage) {
        PhotoPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            photoRejectionReasons = photoRejectionReasons,
            photoRejectionNotes = photoRejectionNotes,
            photoRequestFreeText = photoRequestFreeText,
            uploadPhotoLink = uploadPhotoLink,
            eroContactDetails = with(eroContactDetails) {
                buildContactDetailsDto(
                    localAuthorityName = localAuthorityName,
                    website = website,
                    phone = phone,
                    email = email,
                    address = with(address) {
                        buildAddressDto(
                            street = street,
                            property = property,
                            locality = locality,
                            town = town,
                            area = area,
                            postcode = postcode,
                        )
                    },
                )
            },
        )
    }
}

fun buildIdDocumentPersonalisationDtoFromMessage(
    personalisationMessage: IdDocumentPersonalisation,
    documentRejectionText: String? = null,
): IdDocumentPersonalisationDto {
    return with(personalisationMessage) {
        IdDocumentPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            idDocumentRequestFreeText = idDocumentRequestFreeText,
            documentRejectionText = documentRejectionText,
            eroContactDetails = with(eroContactDetails) {
                buildContactDetailsDto(
                    localAuthorityName = localAuthorityName,
                    website = website,
                    phone = phone,
                    email = email,
                    address = with(address) {
                        buildAddressDto(
                            street = street,
                            property = property,
                            locality = locality,
                            town = town,
                            area = area,
                            postcode = postcode,
                        )
                    },
                )
            },
        )
    }
}

fun buildIdDocumentRequiredPersonalisationDtoFromMessage(
    personalisationMessage: IdDocumentRequiredPersonalisation,
): IdDocumentRequiredPersonalisationDto {
    return with(personalisationMessage) {
        IdDocumentRequiredPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            idDocumentRequiredFreeText = idDocumentRequiredFreeText,
            eroContactDetails = with(eroContactDetails) {
                buildContactDetailsDto(
                    localAuthorityName = localAuthorityName,
                    website = website,
                    phone = phone,
                    email = email,
                    address = with(address) {
                        buildAddressDto(
                            street = street,
                            property = property,
                            locality = locality,
                            town = town,
                            area = area,
                            postcode = postcode,
                        )
                    },
                )
            },
        )
    }
}

fun buildApplicationApprovedPersonalisationDtoFromMessage(
    personalisationMessage: BasePersonalisation,
): ApplicationApprovedPersonalisationDto {
    return with(personalisationMessage) {
        ApplicationApprovedPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            eroContactDetails = with(eroContactDetails) {
                buildContactDetailsDto(
                    localAuthorityName = localAuthorityName,
                    website = website,
                    phone = phone,
                    email = email,
                    address = with(address) {
                        buildAddressDto(
                            street = street,
                            property = property,
                            locality = locality,
                            town = town,
                            area = area,
                            postcode = postcode,
                        )
                    },
                )
            },
        )
    }
}

fun buildPhotoPersonalisationMapFromDto(
    personalisationDto: PhotoPersonalisationDto = buildPhotoPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["photoRejectionReasons"] = photoRejectionReasons
        personalisationMap["photoRejectionNotes"] = photoRejectionNotes ?: ""
        personalisationMap["photoRequestFreeText"] = photoRequestFreeText
        personalisationMap["uploadPhotoLink"] = uploadPhotoLink
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildIdDocumentPersonalisationMapFromDto(
    personalisationDto: IdDocumentPersonalisationDto = buildIdDocumentPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["documentRequestFreeText"] = idDocumentRequestFreeText
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildIdDocumentRequiredPersonalisationMapFromDto(
    personalisationDto: IdDocumentRequiredPersonalisationDto = buildIdDocumentRequiredPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["ninoFailFreeText"] = idDocumentRequiredFreeText
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildApplicationReceivedPersonalisationMapFromDto(
    personalisationDto: ApplicationReceivedPersonalisationDto = buildApplicationReceivedPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["sourceType"] = personalisationSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildApplicationApprovedPersonalisationMapFromDto(
    personalisationDto: ApplicationApprovedPersonalisationDto = buildApplicationApprovedPersonalisationDto(),
) = getCommonDetailsMap(
    personalisationDto.firstName,
    personalisationDto.applicationReference,
    personalisationDto.eroContactDetails,
)

fun buildApplicationRejectedPersonalisationMapFromDto(
    personalisationDto: ApplicationRejectedPersonalisationDto = buildApplicationRejectedPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectionReasonList"] = rejectionReasonList
        personalisationMap["rejectionReasonMessage"] = rejectionReasonMessage ?: ""
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRejectedDocumentPersonalisationMapFromDto(
    personalisationDto: RejectedDocumentPersonalisationDto = buildRejectedDocumentPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectedDocuments"] = documents
        personalisationMap["rejectionMessage"] = rejectedDocumentFreeText ?: ""
        personalisationMap["sourceType"] = personalisationSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRejectedSignaturePersonalisationMapFromDto(
    personalisationDto: RejectedSignaturePersonalisationDto = buildRejectedSignaturePersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectionNotes"] = rejectionNotes ?: ""
        personalisationMap["rejectionReasons"] = rejectionReasons
        personalisationMap["rejectionFreeText"] = rejectionFreeText ?: ""
        personalisationMap["sourceType"] = personalisationSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRequestedSignaturePersonalisationMapFromDto(
    personalisationDto: RequestedSignaturePersonalisationDto = buildRequestedSignaturePersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["freeText"] = freeText ?: ""
        personalisationMap["sourceType"] = personalisationSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRejectedOverseasDocumentPersonalisationMapFromDto(
    personalisationDto: RejectedOverseasDocumentPersonalisationDto = buildRejectedOverseasDocumentTemplatePreviewPersonalisation(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectedDocuments"] = documents
        personalisationMap["rejectionMessage"] = rejectedDocumentFreeText ?: ""
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRequiredOverseasDocumentPersonalisationMapFromDto(
    personalisationDto: RequiredOverseasDocumentPersonalisationDto = buildRequiredOverseasDocumentTemplatePreviewPersonalisation(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["freeText"] = requiredDocumentFreeText ?: ""
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRequiredDocumentPersonalisationMapFromDto(
    personalisationDto: RequiredDocumentPersonalisationDto = buildRequiredDocumentPersonalisation(),
    sourceType: SourceType,
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()

    with(personalisationDto) {
        if (sourceType == SourceType.OVERSEAS) {
            personalisationMap["freeText"] = additionalNotes ?: ""
        } else {
            personalisationMap["additionalNotes"] = additionalNotes ?: ""
        }
        personalisationMap["sourceType"] = personalisationSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildContactDetailsDto(
    localAuthorityName: String = "some-city-council",
    website: String = faker.company().url(),
    phone: String = faker.phoneNumber().cellPhone(),
    email: String = faker.internet().emailAddress(),
    address: AddressDto = buildAddressDto(),
): ContactDetailsDto =
    ContactDetailsDto(
        localAuthorityName = localAuthorityName,
        website = website,
        phone = phone,
        email = email,
        address = address,
    )

fun buildAddressDto(
    street: String = faker.address().streetName(),
    property: String? = faker.address().buildingNumber(),
    locality: String? = faker.address().streetName(),
    town: String? = faker.address().city(),
    area: String? = faker.address().state(),
    postcode: String = faker.address().postcode(),
): AddressDto = AddressDto(
    street = street,
    property = property,
    locality = locality,
    town = town,
    area = area,
    postcode = postcode,
)

fun buildAddressDtoWithOptionalFieldsNull(): AddressDto = buildAddressDto(
    property = null,
    locality = null,
    town = null,
    area = null,
)

fun buildNinoNotMatchedPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    additionalNotes: String? = "Additional Notes",
    sourceType: String = "postal",
): RequiredDocumentPersonalisationDto = RequiredDocumentPersonalisationDto(
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    applicationReference = applicationReference,
    additionalNotes = additionalNotes,
    personalisationSourceTypeString = sourceType,
)

fun buildNinoNotMatchedPersonalisationMapFromDto(
    personalisationDto: RequiredDocumentPersonalisationDto = buildNinoNotMatchedPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["additionalNotes"] = additionalNotes ?: ""
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildBespokeCommPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    sourceType: String = "postal vote",
    subjectHeader: String = faker.yoda().quote(),
    details: String = faker.yoda().quote(),
    whatToDo: String? = faker.yoda().quote(),
    deadline: String? = "You must do this by 17:00 on 07 July 2024 or your postal vote application may be rejected",
): BespokeCommPersonalisationDto = BespokeCommPersonalisationDto(
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    applicationReference = applicationReference,
    personalisationFullSourceTypeString = sourceType,
    subjectHeader = subjectHeader,
    details = details,
    whatToDo = whatToDo,
    deadline = deadline,
)

fun buildBespokeCommPersonalisationMapFromDto(
    personalisationDto: BespokeCommPersonalisationDto = buildBespokeCommPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()

    with(personalisationDto) {
        personalisationMap["subjectHeader"] = subjectHeader
        personalisationMap["giveDetailsFreeText"] = details
        personalisationMap["explainFreeText"] = whatToDo ?: ""
        personalisationMap["whatYouNeedToDo"] = whatToDo != null || deadline != null
        personalisationMap["deadline"] = deadline ?: ""
        personalisationMap["an"] = personalisationFullSourceTypeString == "overseas vote"
        personalisationMap["sourceType"] = personalisationFullSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }

    return personalisationMap
}

fun buildInviteToRegisterPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    sourceType: String = "postal vote",
    freeText: String? = faker.yoda().quote(),
    property: String? = faker.address().buildingNumber(),
    street: String? = faker.address().streetName(),
    town: String? = faker.address().city(),
    area: String? = faker.address().city(),
    locality: String? = faker.address().city(),
    postcode: String? = faker.address().postcode(),
): InviteToRegisterPersonalisationDto = InviteToRegisterPersonalisationDto(
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    applicationReference = applicationReference,
    personalisationFullSourceTypeString = sourceType,
    freeText = freeText,
    property = property,
    street = street,
    town = town,
    area = area,
    locality = locality,
    postcode = postcode,
)

fun buildInviteToRegisterPersonalisationMapFromDto(
    personalisationDto: InviteToRegisterPersonalisationDto = buildInviteToRegisterPersonalisationDto(),
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()

    with(personalisationDto) {
        personalisationMap["freeText"] = freeText ?: ""
        personalisationMap["property"] = property ?: ""
        personalisationMap["street"] = street ?: ""
        personalisationMap["town"] = town ?: ""
        personalisationMap["area"] = area ?: ""
        personalisationMap["locality"] = locality ?: ""
        personalisationMap["postcode"] = postcode ?: ""
        personalisationMap["sourceType"] = personalisationFullSourceTypeString
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }

    return personalisationMap
}

private fun getCommonDetailsMap(
    firstName: String,
    applicationReference: String,
    contactDetailsDto: ContactDetailsDto,
): MutableMap<String, String> {
    val contactDetailsMap = mutableMapOf<String, String>()
    contactDetailsMap["applicationReference"] = applicationReference
    contactDetailsMap["firstName"] = firstName
    return with(contactDetailsDto) {
        contactDetailsMap["LAName"] = localAuthorityName
        contactDetailsMap["eroPhone"] = phone
        contactDetailsMap["eroWebsite"] = website
        contactDetailsMap["eroEmail"] = email
        with(address) {
            contactDetailsMap["eroAddressLine1"] = property ?: ""
            contactDetailsMap["eroAddressLine2"] = street
            contactDetailsMap["eroAddressLine3"] = town ?: ""
            contactDetailsMap["eroAddressLine4"] = area ?: ""
            contactDetailsMap["eroAddressLine5"] = locality ?: ""
            contactDetailsMap["eroPostcode"] = postcode
        }
        contactDetailsMap
    }
}
