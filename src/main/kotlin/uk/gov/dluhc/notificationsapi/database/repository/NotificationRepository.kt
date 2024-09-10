package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.NotificationNotFoundException
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotificationSummary
import uk.gov.dluhc.notificationsapi.database.entity.SOURCE_REFERENCE_INDEX_NAME
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Repository
class NotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val NOTIFICATIONS_TABLE_FULL_SCHEMA = TableSchema.fromBean(Notification::class.java)
        private val NOTIFICATIONS_TABLE_SUMMARY_SCHEMA = TableSchema.fromBean(NotificationSummary::class.java)
    }

    private val notificationsTableFull = client.table(tableConfig.notificationsTableName, NOTIFICATIONS_TABLE_FULL_SCHEMA)
    private val notificationsTableSummary = client.table(tableConfig.notificationsTableName, NOTIFICATIONS_TABLE_SUMMARY_SCHEMA)

    fun saveNotification(notification: Notification) {
        logger.debug("Saving notification for type [${notification.type}], channel: [${notification.channel}]")
        notificationsTableFull.putItem(notification)
    }

    /**
     * Returns the Notification identified by its id (primary partition key)
     */
    fun getNotification(notificationId: UUID): Notification =
        notificationsTableFull.getItem(key(notificationId.toString()))
            ?: throw NotificationNotFoundException(notificationId)

    /**
     * Returns the Notification identified by its id (primary partition key)
     * Restricted by ERO, sourceReference and type of application to control access
     */
    fun getNotificationById(notificationId: UUID, sourceReference: String, sourceType: SourceType, gssCodes: List<String>): Notification {
        val queryRequest = queryRequestWithNotificationId(notificationId.toString(), sourceReference, sourceType, gssCodes).build()

        return notificationsTableFull.index(SOURCE_REFERENCE_INDEX_NAME).query(queryRequest).flatMap { it.items() }
            .getOrElse(0) {
                throw NotificationNotFoundException(notificationId)
            }
    }

    /**
     * Get all Notifications by sourceReference, sourceType and gssCode for one of the specified gssCodes.
     */
    fun getBySourceReferenceAndGssCode(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<Notification> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .build()

        val index = notificationsTableFull.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Get all Notification Summaries by sourceReference and sourceType for one of the specified gssCodes.
     */
    fun getNotificationSummariesBySourceReference(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<NotificationSummary> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes).build()

        val index = notificationsTableSummary.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Get all Notification Summaries by sourceReference and sourceType. Gss codes are not specified.
     * This should not be used for endpoints used by EROs, which should specify the ero and gss code to control access.
     */
    fun getNotificationSummariesBySourceReference(sourceReference: String, sourceType: SourceType): List<NotificationSummary> {
        val queryRequest = queryRequestWithoutGssCodes(sourceReference, sourceType).build()

        val index = notificationsTableSummary.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Remove all Notifications by sourceReference and sourceType.
     */
    fun removeBySourceReference(sourceReference: String, sourceType: SourceType) {
        with(getBySourceReference(sourceReference, sourceType)) {
            logger.debug("Removing [$size] notifications")
            forEach { notification -> notificationsTableFull.deleteItem(notification) }
        }
    }

    /**
     * Private method only intended to be used when removing notifications. It does not require a gssCode because it
     * could have been changed on the original source application, meaning the one we have stored here would no longer
     * match.
     */
    private fun getBySourceReference(sourceReference: String, sourceType: SourceType): List<Notification> {
        val queryRequest = queryRequestWithoutGssCodes(sourceReference, sourceType).build()

        val index = notificationsTableFull.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }
}
