package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressOverseasElectorAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.messaging.models.OverseasElectorAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun aMessageAddress(
    emailAddress: String? = anEmailAddress(),
    postalAddress: MessageAddressPostalAddress? = aMessageAddressPostalAddress(),
    overseasElectorAddress: MessageAddressOverseasElectorAddress? = aMessageAddressOverseasElectorAddress(),
) = MessageAddress(
    emailAddress = emailAddress,
    postalAddress = postalAddress,
    overseasElectorAddress = overseasElectorAddress,
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
            postcode = postcode,
        ),
    )

fun aMessageAddressOverseasElectorAddress(
    addressee: String = faker.name().firstName(),
    addressLine1: String = faker.address().streetName(),
    addressLine2: String? = faker.address().buildingNumber(),
    addressLine3: String? = faker.address().streetName(),
    addressLine4: String? = faker.address().city(),
    addressLine5: String? = faker.address().state(),
    country: String = faker.address().country(),
): MessageAddressOverseasElectorAddress = MessageAddressOverseasElectorAddress(
    address = OverseasElectorAddress(
        addressee = addressee,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        addressLine3 = addressLine3,
        addressLine4 = addressLine4,
        addressLine5 = addressLine5,
        country = country,
    ),
)
