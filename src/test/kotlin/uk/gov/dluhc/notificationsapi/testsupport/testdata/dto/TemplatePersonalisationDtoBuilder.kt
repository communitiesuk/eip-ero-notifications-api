package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
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
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): PhotoPersonalisationDto =
    PhotoPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

fun buildIdDocumentPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequestFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): IdDocumentPersonalisationDto =
    IdDocumentPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = idDocumentRequestFreeText,
        eroContactDetails = eroContactDetails
    )

fun buildIdDocumentRequiredPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    idDocumentRequiredFreeText: String = faker.harryPotter().spell()
): IdDocumentRequiredPersonalisationDto =
    IdDocumentRequiredPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        idDocumentRequiredFreeText = idDocumentRequiredFreeText
    )

fun buildApplicationReceivedPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): ApplicationReceivedPersonalisationDto =
    ApplicationReceivedPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails
    )

fun buildApplicationApprovedPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): ApplicationApprovedPersonalisationDto =
    ApplicationApprovedPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails
    )

fun buildRejectedDocumentPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    rejectedDocumentFreeText: String? = faker.harryPotter().spell(),
    documents: List<String> = listOf(faker.lordOfTheRings().location()),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): RejectedDocumentPersonalisationDto =
    RejectedDocumentPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        documents = documents,
        rejectedDocumentFreeText = rejectedDocumentFreeText,
        eroContactDetails = eroContactDetails
    )

fun buildPhotoPersonalisationDtoFromMessage(
    personalisationMessage: PhotoPersonalisation
): PhotoPersonalisationDto {
    return with(personalisationMessage) {
        PhotoPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
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
                    }
                )
            }
        )
    }
}

fun buildIdDocumentPersonalisationDtoFromMessage(
    personalisationMessage: IdDocumentPersonalisation
): IdDocumentPersonalisationDto {
    return with(personalisationMessage) {
        IdDocumentPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            idDocumentRequestFreeText = idDocumentRequestFreeText,
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
                    }
                )
            }
        )
    }
}

fun buildIdDocumentRequiredPersonalisationDtoFromMessage(
    personalisationMessage: IdDocumentRequiredPersonalisation
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
                    }
                )
            }
        )
    }
}

fun buildApplicationReceivedPersonalisationDtoFromMessage(
    personalisationMessage: BasePersonalisation
): ApplicationReceivedPersonalisationDto {
    return with(personalisationMessage) {
        ApplicationReceivedPersonalisationDto(
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
                    }
                )
            }
        )
    }
}

fun buildApplicationApprovedPersonalisationDtoFromMessage(
    personalisationMessage: BasePersonalisation
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
                    }
                )
            }
        )
    }
}

fun buildPhotoPersonalisationMapFromDto(
    personalisationDto: PhotoPersonalisationDto = buildPhotoPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
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
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildApplicationApprovedPersonalisationMapFromDto(
    personalisationDto: ApplicationApprovedPersonalisationDto = buildApplicationApprovedPersonalisationDto(),
) = getCommonDetailsMap(
    personalisationDto.firstName,
    personalisationDto.applicationReference,
    personalisationDto.eroContactDetails
)

fun buildApplicationRejectedPersonalisationMapFromDto(
    personalisationDto: ApplicationRejectedPersonalisationDto = buildApplicationRejectedPersonalisationDto()
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
    personalisationDto: RejectedDocumentPersonalisationDto = buildRejectedDocumentPersonalisationDto()
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectedDocuments"] = documents
        personalisationMap["rejectionMessage"] = rejectedDocumentFreeText ?: ""
        personalisationMap.putAll(getCommonDetailsMap(firstName, applicationReference, eroContactDetails))
    }
    return personalisationMap
}

fun buildRejectedSignaturePersonalisationMapFromDto(
    personalisationDto: RejectedSignaturePersonalisationDto = buildRejectedSignaturePersonalisationDto()
): Map<String, Any> {
    val personalisationMap = mutableMapOf<String, Any>()
    with(personalisationDto) {
        personalisationMap["rejectionNotes"] = rejectionNotes ?: ""
        personalisationMap["rejectionReasons"] = rejectionReasons
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
        address = address
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
