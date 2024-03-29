package uk.gov.dluhc.notificationsapi.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.mapper.NotificationSummaryMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationsHistoryResponse
import uk.gov.dluhc.notificationsapi.service.SentNotificationsService
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApi

/**
 * REST Controller exposing APIs relating to communications that have been sent.
 */
@RestController
@CrossOrigin
@RequestMapping("/eros/{eroId}/communications")
class SentCommunicationsController(
    private val sentNotificationsService: SentNotificationsService,
    private val notificationSummaryMapper: NotificationSummaryMapper,
    private val sourceTypeMapper: SourceTypeMapper
) {

    @GetMapping("applications/{applicationId}")
    @PreAuthorize(HAS_APPLICATION_SPECIFIC_ERO_ADMIN_AUTHORITY)
    fun getCommunicationHistoryByApplicationId(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
        @RequestParam(required = false, defaultValue = "voter-card") sourceType: SourceTypeApi
    ): CommunicationsHistoryResponse =
        sentNotificationsService.getNotificationSummariesForApplication(
            sourceReference = applicationId,
            eroId = eroId,
            sourceType = sourceTypeMapper.fromApiToDto(sourceType)
        ).map {
            notificationSummaryMapper.toCommunicationsSummaryApi(it)
        }.let {
            CommunicationsHistoryResponse(communications = it)
        }
}
