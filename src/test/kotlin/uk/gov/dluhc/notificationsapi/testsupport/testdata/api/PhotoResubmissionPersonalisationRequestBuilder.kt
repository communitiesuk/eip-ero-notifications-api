package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.PhotoResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getAValidPostcode

fun buildPhotoResubmissionPersonalisationRequest(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    photoRequestFreeText: String = DataFaker.faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetails = buildContactDetailsRequest()
): PhotoResubmissionPersonalisation =
    PhotoResubmissionPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

fun buildContactDetailsRequest(
    localAuthorityName: String = "some-city-council",
    website: String = DataFaker.faker.company().url(),
    phone: String = DataFaker.faker.phoneNumber().cellPhone(),
    email: String = DataFaker.faker.internet().emailAddress(),
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
    street: String = DataFaker.faker.address().streetName(),
    property: String? = DataFaker.faker.address().buildingNumber(),
    locality: String? = DataFaker.faker.address().streetName(),
    town: String? = DataFaker.faker.address().city(),
    area: String? = DataFaker.faker.address().state(),
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
