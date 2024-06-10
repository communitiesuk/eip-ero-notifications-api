package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedSignatureMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyRejectedSignatureMessage(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.PROXY,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: RejectedSignaturePersonalisation = buildRejectedSignaturePersonalisation(),
    toAddress: MessageAddress = aMessageAddress(),
) = SendNotifyRejectedSignatureMessage(
    channel = channel,
    language = language,
    sourceType = sourceType,
    sourceReference = sourceReference,
    gssCode = gssCode,
    requestor = requestor,
    messageType = MessageType.REJECTED_MINUS_SIGNATURE,
    personalisation = personalisation,
    toAddress = toAddress,
)

fun buildRejectedSignaturePersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    rejectionNotes: String? = "Invalid Signature",
    rejectionReasons: List<SignatureRejectionReason> = listOf(
        SignatureRejectionReason.PARTIALLY_MINUS_CUT_MINUS_OFF,
        SignatureRejectionReason.TOO_MINUS_DARK,
        SignatureRejectionReason.OTHER,
    ),
) = RejectedSignaturePersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionNotes = rejectionNotes,
    rejectionReasons = rejectionReasons,
)
