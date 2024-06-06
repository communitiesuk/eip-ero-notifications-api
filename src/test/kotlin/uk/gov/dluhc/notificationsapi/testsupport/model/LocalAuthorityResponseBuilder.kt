package uk.gov.dluhc.notificationsapi.testsupport.model

import uk.gov.dluhc.eromanagementapi.models.Address
import uk.gov.dluhc.eromanagementapi.models.ContactDetails
import uk.gov.dluhc.eromanagementapi.models.LocalAuthorityResponse
import uk.gov.dluhc.notificationsapi.testsupport.aValidEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.aValidLocalAuthorityName
import uk.gov.dluhc.notificationsapi.testsupport.aValidPhoneNumber
import uk.gov.dluhc.notificationsapi.testsupport.aValidWebsite
import uk.gov.dluhc.notificationsapi.testsupport.getRandomGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun buildLocalAuthorityResponse(
    gssCode: String = getRandomGssCode(),
    name: String = aValidLocalAuthorityName(),
    contactDetailsEnglish: ContactDetails = buildContactDetails(),
    contactDetailsWelsh: ContactDetails? = null,
) = LocalAuthorityResponse(
    gssCode = gssCode,
    name = name,
    contactDetailsEnglish = contactDetailsEnglish,
    contactDetailsWelsh = contactDetailsWelsh,
)

fun buildContactDetails(
    websiteAddress: String = aValidWebsite(),
    phoneNumber: String = aValidPhoneNumber(),
    emailAddress: String = aValidEmailAddress(),
    address: Address = buildEroManagementAddress(),
): ContactDetails =
    ContactDetails(
        website = websiteAddress,
        phone = phoneNumber,
        email = emailAddress,
        address = address,
    )

fun buildEroManagementAddress(
    street: String = faker.address().streetName(),
    postcode: String = faker.address().postcode(),
    property: String = faker.address().buildingNumber(),
    town: String = faker.address().city(),
    area: String = faker.address().state(),
    locality: String? = null,
    uprn: String? = null,
): Address =
    Address(
        street = street,
        postcode = postcode,
        property = property,
        area = area,
        town = town,
        locality = locality,
        uprn = uprn,
    )
