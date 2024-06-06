package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.RequestedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRequestedSignatureMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyRequestedSignatureMessage(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.PROXY,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: RequestedSignaturePersonalisation = buildRequestedSignaturePersonalisation(),
    toAddress: MessageAddress = aMessageAddress(),
) = SendNotifyRequestedSignatureMessage(
    channel = channel,
    language = language,
    sourceType = sourceType,
    sourceReference = sourceReference,
    gssCode = gssCode,
    requestor = requestor,
    messageType = MessageType.REQUESTED_MINUS_SIGNATURE,
    personalisation = personalisation,
    toAddress = toAddress,
)

fun buildRequestedSignaturePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    freeText: String? = "Invalid Signature",
) = RequestedSignaturePersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    freeText = freeText,
)
