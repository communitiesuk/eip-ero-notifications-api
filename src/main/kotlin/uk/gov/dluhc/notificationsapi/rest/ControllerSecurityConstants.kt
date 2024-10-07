package uk.gov.dluhc.notificationsapi.rest

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.exception.InvalidSourceTypeException
import uk.gov.dluhc.notificationsapi.models.SourceType.OVERSEAS
import uk.gov.dluhc.notificationsapi.models.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.models.SourceType.PROXY
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

    fun resolveRequiredAuthority(eroId: String, sourceType: String): String =
        when (sourceType) {
            VOTER_MINUS_CARD.value -> "ero-vc-admin-$eroId"
            OVERSEAS.value -> "ero-oe-admin-$eroId"
            POSTAL.value, PROXY.value -> "ero-$sourceType-admin-$eroId"
            else -> throw InvalidSourceTypeException(sourceType)
        }
}
