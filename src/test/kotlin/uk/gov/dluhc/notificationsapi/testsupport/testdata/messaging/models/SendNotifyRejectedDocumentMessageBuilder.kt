package uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models

import uk.gov.dluhc.notificationsapi.messaging.models.ContactDetails
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.DUPLICATE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedDocumentMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildSendNotifyRejectedDocumentMessage(
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.POSTAL,
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    personalisation: RejectedDocumentPersonalisation = buildRejectedDocumentsPersonalisation(),
    channel: NotificationChannel = NotificationChannel.EMAIL,
    toAddress: MessageAddress = aMessageAddress(),
    documentCategory: DocumentCategory = DocumentCategory.IDENTITY
): SendNotifyRejectedDocumentMessage =
    SendNotifyRejectedDocumentMessage(
        language = language,
        sourceType = sourceType,
        sourceReference = sourceReference,
        gssCode = gssCode,
        requestor = requestor,
        messageType = MessageType.REJECTED_MINUS_DOCUMENT,
        personalisation = personalisation,
        channel = channel,
        toAddress = toAddress,
        documentCategory = documentCategory
    )

fun buildRejectedDocumentsPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildContactDetailsMessage(),
    rejectedDocumentMessage: String = "Documents rejected due to being out of date",
    documents: List<RejectedDocument> = listOf(buildRejectedDocumentForPersonalisation())
): RejectedDocumentPersonalisation =
    RejectedDocumentPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        documents = documents,
        rejectedDocumentMessage = rejectedDocumentMessage
    )

fun buildRejectedDocumentForPersonalisation(
    documentType: DocumentType = DocumentType.UTILITY_MINUS_BILL,
    rejectionReasons: List<DocumentRejectionReason> = listOf(DUPLICATE_MINUS_DOCUMENT, DOCUMENT_MINUS_TOO_MINUS_OLD),
    rejectionNotes: String? = "Document rejected due to multiple reasons"
): RejectedDocument = RejectedDocument(
    documentType = documentType,
    rejectionReasons = rejectionReasons,
    rejectionNotes = rejectionNotes
)
