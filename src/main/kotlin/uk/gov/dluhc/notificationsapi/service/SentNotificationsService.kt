package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.NotificationSummaryMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper

private val logger = KotlinLogging.logger {}

/**
 * Class exposing service methods relating to Notifications sent for applications.
 */
@Service
class SentNotificationsService(
    private val eroService: EroService,
    private val notificationRepository: NotificationRepository,
    private val sourceTypeMapper: SourceTypeMapper,
    private val notificationSummaryMapper: NotificationSummaryMapper,
) {

    /**
     * Returns a list of [NotificationSummaryDto]s for the application identified by the specified
     * sourceReference, ERO ID and sourceType.
     */
    fun getNotificationSummariesForApplication(
        sourceReference: String,
        eroId: String,
        sourceType: SourceType,
    ): List<NotificationSummaryDto> =
        eroService.lookupGssCodesForEro(eroId).let { gssCodes ->
            notificationRepository.getNotificationSummariesBySourceReference(
                sourceReference = sourceReference,
                sourceType = sourceTypeMapper.fromDtoToEntity(sourceType),
                gssCodes = gssCodes,
            )
        }.sortedByDescending {
            it.sentAt
        }.map {
            notificationSummaryMapper.toNotificationSummaryDto(it)
        }.also {
            logger.info { "Returning ${it.count()} NotificationSummaries for $sourceType application $sourceReference" }
        }
}
