package uk.gov.dluhc.notificationsapi.rest

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationConfirmationHistoryResponse
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import uk.gov.dluhc.notificationsapi.service.CommunicationConfirmationsService
import javax.validation.Valid

/**
 * REST Controller exposing APIs relating to offline communication confirmations.
 */
@RestController
@CrossOrigin
@RequestMapping("/eros/{eroId}/communications")
class CommunicationConfirmationsController(
    private val communicationConfirmationsService: CommunicationConfirmationsService,
    private val communicationConfirmationMapper: CommunicationConfirmationMapper,
) {

    @PostMapping("anonymous-applications/{applicationId}")
    @PreAuthorize(HAS_ERO_VC_ANONYMOUS_ADMIN_AUTHORITY)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun createOfflineCommunicationConfirmation(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
        @Valid @RequestBody request: CreateOfflineCommunicationConfirmationRequest,
        authentication: Authentication,
    ) {
        val dto = communicationConfirmationMapper.fromApiToDto(
            eroId = eroId,
            sourceReference = applicationId,
            requestor = authentication.name,
            request = request,
        )
        communicationConfirmationsService.saveCommunicationConfirmation(dto)
    }

    @GetMapping("anonymous-applications/{applicationId}")
    @PreAuthorize(HAS_ERO_VC_ANONYMOUS_ADMIN_AUTHORITY)
    fun getOfflineCommunicationConfirmations(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
    ): CommunicationConfirmationHistoryResponse =
        TODO("EIP1-4400 - AEDs - retrieve list of offline communications - API")
}
