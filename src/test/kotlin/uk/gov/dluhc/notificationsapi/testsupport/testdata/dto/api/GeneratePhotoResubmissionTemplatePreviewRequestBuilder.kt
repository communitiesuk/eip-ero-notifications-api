package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api

import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.PhotoResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildGeneratePhotoResubmissionTemplatePreviewRequest(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    personalisation: PhotoResubmissionPersonalisation = buildPhotoResubmissionPersonalisation()
): GeneratePhotoResubmissionTemplatePreviewRequest =
    GeneratePhotoResubmissionTemplatePreviewRequest(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )

fun buildPhotoResubmissionPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetails = buildContactDetails()
): PhotoResubmissionPersonalisation =
    PhotoResubmissionPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

fun buildContactDetails(
    localAuthorityName: String = "some-city-council",
    website: String = faker.company().url(),
    phone: String = faker.phoneNumber().cellPhone(),
    email: String = faker.internet().emailAddress(),
    address: Address = buildAddress(),
): ContactDetails =
    ContactDetails(
        localAuthorityName = localAuthorityName,
        website = website,
        phone = phone,
        email = email,
        address = address
    )

fun buildAddress(
    street: String = faker.address().streetName(),
    property: String = faker.address().buildingNumber(),
    locality: String = faker.address().streetName(),
    town: String = faker.address().city(),
    area: String = faker.address().state(),
    postcode: String = faker.address().postcode(),
): Address = Address(
    street = street,
    property = property,
    locality = locality,
    town = town,
    area = area,
    postcode = postcode,
)
