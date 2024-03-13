package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.messaging.models.Address
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressOverseasElectorAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddressPostalAddress
import uk.gov.dluhc.notificationsapi.messaging.models.OverseasElectorAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun buildNotificationDestination(
    emailAddress: String? = faker.internet().emailAddress(),
    postalAddress: MessageAddressPostalAddress? = buildMessageAddressPostalAddress(),
    overseasElectorAddress: MessageAddressOverseasElectorAddress = buildMessageAddressOverseasElectorAddress()
): MessageAddress = MessageAddress(
    emailAddress = emailAddress,
    postalAddress = postalAddress,
    overseasElectorAddress = overseasElectorAddress
)

fun buildMessageAddressPostalAddress(
    addressee: String? = faker.name().firstName(),
    address: Address = buildMessageAddress()
): MessageAddressPostalAddress = MessageAddressPostalAddress(
    addressee = addressee,
    address = address
)

fun buildMessageAddressOverseasElectorAddress(
    address: OverseasElectorAddress = buildOverseasAddressMessage()
): MessageAddressOverseasElectorAddress = MessageAddressOverseasElectorAddress(
    address = address
)
