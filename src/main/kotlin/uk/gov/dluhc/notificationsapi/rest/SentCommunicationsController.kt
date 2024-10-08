package uk.gov.dluhc.notificationsapi.rest

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.exception.InvalidUuidFormatException
import uk.gov.dluhc.notificationsapi.mapper.NotificationApiMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationSummaryMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationsHistoryResponse
import uk.gov.dluhc.notificationsapi.models.SentCommunicationResponse
import uk.gov.dluhc.notificationsapi.service.SentNotificationsService
import java.util.UUID

/**
 * REST Controller exposing APIs relating to communications that have been sent.
 */
@RestController
@CrossOrigin
@RequestMapping("/eros/{eroId}/communications")
class SentCommunicationsController(
    private val sentNotificationsService: SentNotificationsService,
    private val notificationSummaryMapper: NotificationSummaryMapper,
    private val notificationApiMapper: NotificationApiMapper,
    private val sourceTypeMapper: SourceTypeMapper,
) {

    @GetMapping("applications/{applicationId}")
    @PreAuthorize(HAS_APPLICATION_SPECIFIC_ERO_ADMIN_AUTHORITY)
    fun getCommunicationHistoryByApplicationId(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
        @RequestParam(required = false, defaultValue = "voter-card") sourceType: String,
    ): CommunicationsHistoryResponse =
        sentNotificationsService.getNotificationSummariesForApplication(
            sourceReference = applicationId,
            eroId = eroId,
            sourceType = sourceTypeMapper.fromApiValueToDto(sourceType),
        ).map {
            notificationSummaryMapper.toCommunicationsSummaryApi(it)
        }.let {
            CommunicationsHistoryResponse(communications = it)
        }

    @GetMapping("applications/{applicationId}/{communicationId}")
    @PreAuthorize(HAS_APPLICATION_SPECIFIC_ERO_ADMIN_AUTHORITY)
    fun getSentCommunication(
        @PathVariable eroId: String,
        @PathVariable applicationId: String,
        @PathVariable communicationId: String,
        @RequestParam(required = true) sourceType: String,
    ): SentCommunicationResponse {
        try {
            return sentNotificationsService.getNotificationByIdEroAndType(
                notificationId = UUID.fromString(communicationId),
                eroId = eroId,
                sourceReference = applicationId,
                sourceType = sourceTypeMapper.fromApiValueToDto(sourceType),
            ).let {
                notificationApiMapper.toSentCommunicationsApi(it)
            }
        } catch (e: IllegalArgumentException) {
            throw InvalidUuidFormatException(communicationId)
        }
    }
}
