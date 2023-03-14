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
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.CommunicationConfirmationMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationSummaryMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationsHistoryResponse
import uk.gov.dluhc.notificationsapi.models.CreateOfflineCommunicationConfirmationRequest
import uk.gov.dluhc.notificationsapi.service.CommunicationConfirmationsService
import uk.gov.dluhc.notificationsapi.service.SentNotificationsService
import javax.validation.Valid

/**
 * REST Controller exposing APIs relating to communications that have been sent.
 */
@RestController
@CrossOrigin
@RequestMapping("/eros/{eroId}/communications")
class SentCommunicationsController(
    private val sentNotificationsService: SentNotificationsService,
    private val communicationConfirmationsService: CommunicationConfirmationsService,
    private val notificationSummaryMapper: NotificationSummaryMapper,
    private val communicationConfirmationMapper: CommunicationConfirmationMapper,
) {

    @GetMapping("applications/{applicationId}")
    @PreAuthorize(HAS_ERO_VC_ADMIN_AUTHORITY)
    fun getCommunicationHistoryByApplicationId(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
    ): CommunicationsHistoryResponse =
        sentNotificationsService.getNotificationSummariesForApplication(
            sourceReference = applicationId,
            eroId = eroId,
            sourceType = SourceType.VOTER_CARD
        ).map {
            notificationSummaryMapper.toCommunicationsSummaryApi(it)
        }.let {
            CommunicationsHistoryResponse(communications = it)
        }

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
}
