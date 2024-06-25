package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildApplicationRejectedPersonalisation(
    rejectionReasonList: List<ApplicationRejectionReason> = listOf(
        INCOMPLETE_MINUS_APPLICATION,
        NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT,
        OTHER,
    ),
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    rejectionReasonMessage: String = "Application rejected for incomplete information and no response from the applicant",
): ApplicationRejectedPersonalisation =
    ApplicationRejectedPersonalisation(
        rejectionReasonList = rejectionReasonList,
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        rejectionReasonMessage = rejectionReasonMessage,
    )
