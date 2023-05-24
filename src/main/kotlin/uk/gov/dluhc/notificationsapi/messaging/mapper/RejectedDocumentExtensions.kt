package uk.gov.dluhc.notificationsapi.messaging.mapper

import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument

/**
 * Extension property on RejectedDocument to return the document rejection reasons, excluding OTHER
 * This is because whilst OTHER is a valid rejection reason the ERO can make, it is not to be used in the decision
 * about which gov.uk template to use, or rendered in the bulleted list of rejection reasons in the rendered template.
 */
val RejectedDocument.rejectionReasonsExcludingOther: List<DocumentRejectionReason>
    get() = this.rejectionReasons.filter { it != OTHER }
