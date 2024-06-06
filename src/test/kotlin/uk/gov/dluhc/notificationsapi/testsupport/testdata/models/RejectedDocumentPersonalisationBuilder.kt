package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentType
import uk.gov.dluhc.notificationsapi.models.DocumentType.UTILITY_MINUS_BILL
import uk.gov.dluhc.notificationsapi.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildRejectedDocumentPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    documents: List<RejectedDocument> = listOf(buildRejectedDocument()),
    rejectedDocumentFreeText: String? = faker.harryPotter().spell(),
) = RejectedDocumentPersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    documents = documents,
    rejectedDocumentFreeText = rejectedDocumentFreeText,
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
