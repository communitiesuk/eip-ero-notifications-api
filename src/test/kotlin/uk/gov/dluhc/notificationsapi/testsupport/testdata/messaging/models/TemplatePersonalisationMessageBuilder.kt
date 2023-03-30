package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getAValidPostcode

fun buildPhotoPersonalisationMessage(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetails = buildContactDetailsMessage()
): PhotoPersonalisation =
    PhotoPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

fun buildIdDocumentPersonalisationMessage(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequestFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage()
): IdDocumentPersonalisation =
    IdDocumentPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = idDocumentRequestFreeText,
        eroContactDetails = eroContactDetails
    )

fun buildIdDocumentRequiredPersonalisationMessage(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequiredFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage()
): IdDocumentRequiredPersonalisation =
    IdDocumentRequiredPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequiredFreeText = idDocumentRequiredFreeText,
        eroContactDetails = eroContactDetails
    )

fun buildContactDetailsMessage(
    localAuthorityName: String = "some-city-council",
    website: String = faker.company().url(),
    phone: String = faker.phoneNumber().cellPhone(),
    email: String = faker.internet().emailAddress(),
    address: Address = buildAddressMessage(),
): ContactDetails =
    ContactDetails(
        localAuthorityName = localAuthorityName,
        website = website,
        phone = phone,
        email = email,
        address = address
    )

fun buildAddressMessage(
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

fun buildAddressMessageWithOptionalParamsNull(): Address = buildAddressMessage(
    property = null,
    locality = null,
    town = null,
    area = null,
)
