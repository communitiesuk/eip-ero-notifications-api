package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

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

fun buildPhotoPersonalisationMapFromDto(
    personalisationDto: PhotoPersonalisationDto = buildPhotoPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["applicationReference"] = applicationReference
        personalisationMap["firstName"] = firstName
        personalisationMap["photoRequestFreeText"] = photoRequestFreeText
        personalisationMap["uploadPhotoLink"] = uploadPhotoLink
        with(eroContactDetails) {
            personalisationMap["LAName"] = localAuthorityName
            personalisationMap["eroPhone"] = phone
            personalisationMap["eroWebsite"] = website
            personalisationMap["eroEmail"] = email
            with(address) {
                personalisationMap["eroAddressLine1"] = property ?: ""
                personalisationMap["eroAddressLine2"] = street
                personalisationMap["eroAddressLine3"] = town ?: ""
                personalisationMap["eroAddressLine4"] = area ?: ""
                personalisationMap["eroAddressLine5"] = locality ?: ""
                personalisationMap["eroPostcode"] = postcode
            }
        }
    }
    return personalisationMap
}

fun buildIdDocumentPersonalisationMapFromDto(
    personalisationDto: IdDocumentPersonalisationDto = buildIdDocumentPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["applicationReference"] = applicationReference
        personalisationMap["firstName"] = firstName
        personalisationMap["documentRequestFreeText"] = idDocumentRequestFreeText
        with(eroContactDetails) {
            personalisationMap["LAName"] = localAuthorityName
            personalisationMap["eroPhone"] = phone
            personalisationMap["eroWebsite"] = website
            personalisationMap["eroEmail"] = email
            with(address) {
                personalisationMap["eroAddressLine1"] = property ?: ""
                personalisationMap["eroAddressLine2"] = street
                personalisationMap["eroAddressLine3"] = town ?: ""
                personalisationMap["eroAddressLine4"] = area ?: ""
                personalisationMap["eroAddressLine5"] = locality ?: ""
                personalisationMap["eroPostcode"] = postcode
            }
        }
    }
    return personalisationMap
}

fun buildApplicationApprovedPersonalisationMapFromDto(
    personalisationDto: ApplicationApprovedPersonalisationDto = buildApplicationApprovedPersonalisationDto(),
): Map<String, String> {
    val personalisationMap = mutableMapOf<String, String>()
    with(personalisationDto) {
        personalisationMap["applicationReference"] = applicationReference
        personalisationMap["firstName"] = firstName
        with(eroContactDetails) {
            personalisationMap["LAName"] = localAuthorityName
            personalisationMap["eroPhone"] = phone
            personalisationMap["eroWebsite"] = website
            personalisationMap["eroEmail"] = email
            with(address) {
                personalisationMap["eroAddressLine1"] = property ?: ""
                personalisationMap["eroAddressLine2"] = street
                personalisationMap["eroAddressLine3"] = town ?: ""
                personalisationMap["eroAddressLine4"] = area ?: ""
                personalisationMap["eroAddressLine5"] = locality ?: ""
                personalisationMap["eroPostcode"] = postcode
            }
        }
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
