package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.messaging.models.OverseasAddress
import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.messaging.models.Address as AddressMessage

fun buildAddress(
    property: String? = faker.address().buildingNumber(),
    street: String = faker.address().streetName(),
    town: String? = faker.address().city(),
    area: String? = faker.address().state(),
    locality: String? = faker.address().streetName(),
    postcode: String = faker.address().postcode(),
) = Address(
    property = property,
    street = street,
    town = town,
    area = area,
    locality = locality,
    postcode = postcode,
)

fun buildMessageAddress(
    property: String? = faker.address().buildingNumber(),
    street: String = faker.address().streetName(),
    town: String? = faker.address().city(),
    area: String? = faker.address().state(),
    locality: String? = faker.address().streetName(),
    postcode: String = faker.address().postcode(),
) = AddressMessage(
    property = property,
    street = street,
    town = town,
    area = area,
    locality = locality,
    postcode = postcode,
)

fun buildOverseasAddressMessage(
    addressLine1: String = faker.address().streetName(),
    addressLine2: String? = faker.address().buildingNumber(),
    addressLine3: String? = faker.address().streetName(),
    addressLine4: String? = faker.address().city(),
    addressLine5: String? = faker.address().state(),
    country: String = faker.address().country(),
) = OverseasAddress(
    addressLine1 = addressLine1,
    addressLine2 = addressLine2,
    addressLine3 = addressLine3,
    addressLine4 = addressLine4,
    addressLine5 = addressLine5,
    country = country,
)
