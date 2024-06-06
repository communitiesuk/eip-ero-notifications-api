package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType.UTILITY_MINUS_BILL
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyIdDocumentResubmissionMessage(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.VOTER_MINUS_CARD,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: IdDocumentPersonalisation = buildIdDocumentPersonalisationMessage(),
    toAddress: MessageAddress = aMessageAddress(),
): SendNotifyIdDocumentResubmissionMessage =
    SendNotifyIdDocumentResubmissionMessage(
        channel = channel,
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION,
        personalisation = personalisation,
        toAddress = toAddress,
    )

fun buildIdDocumentPersonalisationMessage(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    idDocumentRequestFreeText: String = faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    rejectedDocuments: List<RejectedDocument> = listOf(buildRejectedDocument()),
): IdDocumentPersonalisation =
    IdDocumentPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = idDocumentRequestFreeText,
        eroContactDetails = eroContactDetails,
        rejectedDocuments = rejectedDocuments,
    )

fun buildRejectedDocument(
    documentType: DocumentType = UTILITY_MINUS_BILL,
    rejectionReasons: List<DocumentRejectionReason> = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
    rejectionNotes: String? = faker.harryPotter().spell(),
) = RejectedDocument(
    documentType = documentType,
    rejectionReasons = rejectionReasons,
    rejectionNotes = rejectionNotes,
)

fun aSendNotifyIdDocumentResubmissionMessage() = buildSendNotifyIdDocumentResubmissionMessage()
