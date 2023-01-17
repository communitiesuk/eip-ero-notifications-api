package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkException
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.RemoveNotificationsDto
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper

private val logger = KotlinLogging.logger {}

/**
 * Service responsible for removing notifications data.
 */
@Service
class RemoveNotificationsService(
    private val notificationRepository: NotificationRepository,
    private val sourceTypeMapper: SourceTypeMapper,
) {
    /**
     * Removes any notifications belonging to an application.
     *
     * @param removeNotificationsDto DTO containing the application's ID and GSS code.
     */
    fun remove(removeNotificationsDto: RemoveNotificationsDto) {
        try {
            with(removeNotificationsDto) {
                val sourceType = sourceTypeMapper.toSourceTypeEntity(this.sourceType)
                notificationRepository.removeBySourceReference(sourceReference, sourceType, gssCode)
            }
        } catch (ex: SdkException) {
            logger.error { "Error attempting to remove notifications: $ex" }
            throw ex
        }
    }
}
