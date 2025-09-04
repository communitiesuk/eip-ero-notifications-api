package uk.gov.dluhc.notificationsapi.mapper

import uk.gov.dluhc.notificationsapi.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation

/**
 * Extension property on RejectedSignature to return the signature rejection reasons, excluding OTHER
 * This is because whilst OTHER is a valid rejection reason the ERO can make, it is not to be used in the decision
 * about which gov.uk template to use, or rendered in the bulleted list of rejection reasons in the rendered template.
 */
val RejectedSignaturePersonalisation.rejectionReasonsExcludingOther: List<SignatureRejectionReason>
    get() = this.rejectionReasons.filter { it != OTHER }

val SignatureResubmissionPersonalisation.rejectionReasonsExcludingOther: List<SignatureRejectionReason>
    get() = this.rejectionReasons.filter { it != OTHER }
