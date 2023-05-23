package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun aMessageAddress(
    emailAddress: String? = anEmailAddress(),
    postalAddress: MessageAddressPostalAddress? = aMessageAddressPostalAddress()
) = MessageAddress(
    emailAddress = emailAddress,
    postalAddress = postalAddress
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
