package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildIdDocumentPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    idDocumentRequestFreeText: String = faker.harryPotter().spell(),
    rejectedDocuments: List<RejectedDocument>? = listOf(buildRejectedDocument()),
) = IdDocumentPersonalisation(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    idDocumentRequestFreeText = idDocumentRequestFreeText,
    rejectedDocuments = rejectedDocuments,
)
