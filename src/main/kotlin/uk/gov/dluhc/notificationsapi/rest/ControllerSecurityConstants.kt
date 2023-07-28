package uk.gov.dluhc.notificationsapi.rest

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.models.SourceType.OVERSEAS
import uk.gov.dluhc.notificationsapi.models.SourceType.VOTER_MINUS_CARD

const val HAS_ERO_VC_ANONYMOUS_ADMIN_AUTHORITY = """
        hasAnyAuthority("ero-vc-anonymous-admin-".concat(#eroId))
    """
const val HAS_ERO_VC_ADMIN_AUTHORITY = """
        hasAnyAuthority("ero-vc-admin-".concat(#eroId))
    """
const val HAS_ERO_VC_ADMIN_OR_VC_VIEWER_AUTHORITY = """
        hasAnyAuthority("ero-vc-admin-".concat(#eroId), 
                        "ero-vc-viewer-".concat(#eroId)
                        )
        """

const val HAS_APPLICATION_SPECIFIC_ERO_ADMIN_AUTHORITY = """
        hasAnyAuthority(@authorityHelper.resolveRequiredAuthority(#eroId, #sourceType))
    """

@Component
class AuthorityHelper {

    fun resolveRequiredAuthority(eroId: String, sourceType: SourceType): String =
        when (sourceType) {
            VOTER_MINUS_CARD -> "ero-vc-admin-$eroId"
            OVERSEAS -> "ero-oe-admin-$eroId"
            else -> "ero-${sourceType.value}-admin-$eroId"
        }
}
