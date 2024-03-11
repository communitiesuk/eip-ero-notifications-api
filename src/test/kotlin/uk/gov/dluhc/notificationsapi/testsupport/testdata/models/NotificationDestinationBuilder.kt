package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressOverseasAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.messaging.models.OverseasAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun buildNotificationDestination(
    emailAddress: String? = faker.internet().emailAddress(),
    postalAddress: MessageAddressPostalAddress? = buildMessageAddressPostalAddress(),
    overseasAddress: MessageAddressOverseasAddress = buildMessageAddressOverseasAddress()
): MessageAddress = MessageAddress(
    emailAddress = emailAddress,
    postalAddress = postalAddress,
    overseasAddress = overseasAddress
)

fun buildMessageAddressPostalAddress(
    addressee: String? = faker.name().firstName(),
    address: Address = buildMessageAddress()
): MessageAddressPostalAddress = MessageAddressPostalAddress(
    addressee = addressee,
    address = address
)

fun buildMessageAddressOverseasAddress(
    addressee: String? = faker.name().firstName(),
    address: OverseasAddress = buildOverseasAddressMessage()
): MessageAddressOverseasAddress = MessageAddressOverseasAddress(
    addressee = addressee,
    address = address
)
