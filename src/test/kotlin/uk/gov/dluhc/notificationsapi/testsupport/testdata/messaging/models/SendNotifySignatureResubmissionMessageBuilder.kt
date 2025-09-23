package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifySignatureResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import java.time.LocalDate

fun buildSendNotifySignatureResubmissionMessage(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: SignatureResubmissionPersonalisation = buildSignatureResubmissionPersonalisation(),
    toAddress: MessageAddress = aMessageAddress(),
) = SendNotifySignatureResubmissionMessage(
    channel = channel,
    language = language,
    sourceType = sourceType,
    sourceReference = sourceReference,
    gssCode = gssCode,
    requestor = requestor,
    personalisation = personalisation,
    toAddress = toAddress,
    messageType = MessageType.SIGNATURE_MINUS_RESUBMISSION,
)

fun buildSignatureResubmissionPersonalisation(
    isRejected: Boolean = true,
    uploadSignatureLink: String = faker.internet().url(),
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    rejectionReasons: List<SignatureRejectionReason> = listOf(SignatureRejectionReason.TOO_MINUS_DARK, SignatureRejectionReason.HAS_MINUS_SHADOWS),
    deadlineDate: LocalDate? = LocalDate.of(2025, 10, 30),
    deadlineTime: String? = "12PM",
    rejectionNotes: String? = "Rejection Notes",
    rejectionFreeText: String? = "Reject Free Text",
) = SignatureResubmissionPersonalisation(
    isRejected = isRejected,
    uploadSignatureLink = uploadSignatureLink,
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionReasons = rejectionReasons,
    deadlineDate = deadlineDate,
    deadlineTime = deadlineTime,
    rejectionNotes = rejectionNotes,
    rejectionFreeText = rejectionFreeText,
)
