package uk.gov.dluhc.notificationsapi.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.models.CommunicationsHistoryResponse

/**
 * REST Controller exposing APIs relating to communications that have been sent.
 */
@RestController
class SentCommunicationsController {

    companion object {
        const val ERO_VC_ADMIN_GROUP_PREFIX = "ero-vc-admin-"
    }

    @GetMapping("/eros/{eroId}/communications/applications/{applicationId}")
    @PreAuthorize(
        """
        hasAnyAuthority(
            T(uk.gov.dluhc.notificationsapi.rest.SentCommunicationsController).ERO_VC_ADMIN_GROUP_PREFIX.concat(#eroId)
        )
        """
    )
    fun getCommunicationHistoryByApplicationId(
        @PathVariable eroId: String,
        @PathVariable applicationId: String
    ): CommunicationsHistoryResponse {
        TODO("not yet implemented")
    }
}
