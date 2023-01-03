package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.IdDocumentResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildIdDocumentResubmissionPersonalisationRequest(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    photoRequestFreeText: String = DataFaker.faker.harryPotter().spell(),
    eroContactDetails: ContactDetails = buildContactDetailsRequest()
): IdDocumentResubmissionPersonalisation =
    IdDocumentResubmissionPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        idDocumentRequestFreeText = photoRequestFreeText,
        eroContactDetails = eroContactDetails
    )
