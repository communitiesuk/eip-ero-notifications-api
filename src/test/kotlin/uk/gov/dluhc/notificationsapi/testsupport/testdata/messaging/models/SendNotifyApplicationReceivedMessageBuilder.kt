package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyApplicationReceivedMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    messageType: MessageType = MessageType.APPLICATION_MINUS_RECEIVED,
    personalisation: BasePersonalisation = buildApplicationReceivedPersonalisation(),
    toAddress: MessageAddress = aMessageAddress(),
): SendNotifyApplicationReceivedMessage =
    SendNotifyApplicationReceivedMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = messageType,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun buildApplicationReceivedPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
): BasePersonalisation =
    BasePersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
    )

fun aSendNotifyApplicationReceivedMessage() = buildSendNotifyApplicationReceivedMessage()
