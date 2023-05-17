package uk.gov.dluhc.notificationsapi.mapper

import uk.gov.dluhc.notificationsapi.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER

/**
 * Extension property on PhotoPersonalisation to return the photo rejection reasons, excluding OTHER
 * This is because whilst OTHER is a valid rejection reason the ERO can make, it is not to be used in the decision
 * about which gov.uk template to use, or rendered in the bulleted list of rejection reasons in the rendered template.
 */
val PhotoPersonalisation.photoRejectionReasonsExcludingOther: List<PhotoRejectionReason>
    get() = photoRejectionReasons.filter { it != OTHER }
