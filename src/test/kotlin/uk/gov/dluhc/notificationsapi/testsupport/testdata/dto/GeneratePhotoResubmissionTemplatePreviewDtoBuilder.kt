package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.AddressDto
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildGeneratePhotoResubmissionTemplatePreviewDto(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoResubmissionPersonalisationDto = buildPhotoResubmissionPersonalisationDto()
): GeneratePhotoResubmissionTemplatePreviewDto =
    GeneratePhotoResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )

fun buildPhotoResubmissionPersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    photoRequestFreeText: String = faker.harryPotter().spell(),
    uploadPhotoLink: String = "http://localhost:8080/eros/photo/398c1be2-7950-48a2-aca8-14cb9276a673",
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto()
): PhotoResubmissionPersonalisationDto =
    PhotoResubmissionPersonalisationDto(
        applicationReference = applicationReference,
        firstName = firstName,
        photoRequestFreeText = photoRequestFreeText,
        uploadPhotoLink = uploadPhotoLink,
        eroContactDetails = eroContactDetails
    )

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
    property: String = faker.address().buildingNumber(),
    locality: String = faker.address().streetName(),
    town: String = faker.address().city(),
    area: String = faker.address().state(),
    postcode: String = faker.address().postcode(),
): AddressDto = AddressDto(
    street = street,
    property = property,
    locality = locality,
    town = town,
    area = area,
    postcode = postcode,
)
