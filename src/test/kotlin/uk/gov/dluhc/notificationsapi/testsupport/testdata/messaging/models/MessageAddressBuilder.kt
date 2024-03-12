package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressOverseasAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.messaging.models.OverseasAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun aMessageAddress(
    emailAddress: String? = anEmailAddress(),
    postalAddress: MessageAddressPostalAddress? = aMessageAddressPostalAddress(),
    overseasAddress: MessageAddressOverseasAddress? = aMessageAddressOverseasAddress()
) = MessageAddress(
    emailAddress = emailAddress,
    postalAddress = postalAddress,
    overseasAddress = overseasAddress
)

fun aMessageAddressPostalAddress(
    addressee: String = faker.name().firstName(),
    property: String = faker.address().streetName(),
    street: String = faker.address().buildingNumber(),
    town: String? = faker.address().streetName(),
    area: String? = faker.address().city(),
    locality: String? = faker.address().state(),
    postcode: String = faker.address().postcode(),
): MessageAddressPostalAddress =
    MessageAddressPostalAddress(
        addressee = addressee,
        address = Address(
            property = property,
            street = street,
            town = town,
            area = area,
            locality = locality,
            postcode = postcode
        ),
    )

fun aMessageAddressOverseasAddress(
    addressee: String = faker.name().firstName(),
    addressLine1: String = faker.address().streetName(),
    addressLine2: String? = faker.address().buildingNumber(),
    addressLine3: String? = faker.address().streetName(),
    addressLine4: String? = faker.address().city(),
    addressLine5: String? = faker.address().state(),
    country: String = faker.address().country(),
): MessageAddressOverseasAddress = MessageAddressOverseasAddress(
    addressee = addressee,
    address = OverseasAddress(
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        addressLine3 = addressLine3,
        addressLine4 = addressLine4,
        addressLine5 = addressLine5,
        country = country,
    )
)
