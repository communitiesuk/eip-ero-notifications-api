package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import java.time.LocalDate

fun buildSignatureResubmissionPersonalisation(
    rejectionReasons: List<SignatureRejectionReason> = listOf(
        SignatureRejectionReason.HAS_MINUS_SHADOWS,
        SignatureRejectionReason.WRONG_MINUS_SIZE,
        SignatureRejectionReason.OTHER,
    ),
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetails = buildEroContactDetails(),
    deadlineDate: LocalDate? = null,
    deadlineTime: String? = null,
    uploadSignatureLink: String = "https://postal-vote.service.gov.uk/resubmit-signature?referenceNumber=A123456789",
    rejectionNotes: String? = "Signature rejected since cropping was poor.",
    rejectionFreeText: String? = "Please be more careful with the scan chosen",
): SignatureResubmissionPersonalisation =
    SignatureResubmissionPersonalisation(
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        rejectionReasons = rejectionReasons,
        deadlineDate = deadlineDate,
        deadlineTime = deadlineTime,
        uploadSignatureLink = uploadSignatureLink,
        rejectionNotes = rejectionNotes,
        rejectionFreeText = rejectionFreeText,
    )
