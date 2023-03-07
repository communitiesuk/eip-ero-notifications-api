package uk.gov.dluhc.notificationsapi.rest

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
