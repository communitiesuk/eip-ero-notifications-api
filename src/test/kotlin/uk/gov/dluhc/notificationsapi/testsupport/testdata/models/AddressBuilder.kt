package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

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
