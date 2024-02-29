package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getAValidPostcode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument

fun buildPhotoResubmissionPersonalisationRequest(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRejectionReasons: List<PhotoRejectionReason> = listOf(PhotoRejectionReason.OTHER_MINUS_OBJECTS_MINUS_OR_MINUS_PEOPLE_MINUS_IN_MINUS_PHOTO),
    photoRejectionNotes: String? = faker.harryPotter().spell(),
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetails = buildContactDetailsRequest()
): PhotoPersonalisation =
    PhotoPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRejectionReasons = photoRejectionReasons,
        photoRejectionNotes = photoRejectionNotes,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

fun buildIdDocumentResubmissionPersonalisationRequest(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRequestFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest(),
    rejectedDocuments: List<RejectedDocument> = listOf(buildRejectedDocument()),
): IdDocumentPersonalisation =
    IdDocumentPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = photoRequestFreeText,
        eroContactDetails = eroContactDetails,
        rejectedDocuments = rejectedDocuments
    )

fun buildContactDetailsRequest(
    localAuthorityName: String = "some-city-council",
    website: String = faker.company().url(),
    phone: String = faker.phoneNumber().cellPhone(),
    email: String = faker.internet().emailAddress(),
    address: Address = buildAddressRequest(),
): ContactDetails =
    ContactDetails(
        localAuthorityName = localAuthorityName,
        website = website,
        phone = phone,
        email = email,
        address = address
    )

fun buildAddressRequest(
    street: String = faker.address().streetName(),
    property: String? = faker.address().buildingNumber(),
    locality: String? = faker.address().streetName(),
    town: String? = faker.address().city(),
    area: String? = faker.address().state(),
    postcode: String = getAValidPostcode(),
): Address = Address(
    street = street,
    property = property,
    locality = locality,
    town = town,
    area = area,
    postcode = postcode,
)

fun buildAddressRequestWithOptionalParamsNull(): Address = buildAddressRequest(
    property = null,
    locality = null,
    town = null,
    area = null,
)
