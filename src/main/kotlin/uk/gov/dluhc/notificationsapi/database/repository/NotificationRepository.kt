package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
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
        private val NOTIFICATIONS_TABLE_SCHEMA = TableSchema.fromBean(Notification::class.java)
        private val NOTIFICATION_SUMMARIES_TABLE_SCHEMA = TableSchema.fromBean(NotificationSummary::class.java)
    }

    private val notificationsTable = client.table(tableConfig.notificationsTableName, NOTIFICATIONS_TABLE_SCHEMA)
    private val notificationsSummaryTable = client.table(tableConfig.notificationsTableName, NOTIFICATION_SUMMARIES_TABLE_SCHEMA)

    fun saveNotification(notification: Notification) {
        logger.debug("Saving notification for type [${notification.type}], channel: [${notification.channel}]")
        notificationsTable.putItem(notification)
    }

    /**
     * Returns the Notification identified by its id (primary partition key)
     */
    fun getNotification(notificationId: UUID): Notification =
        notificationsTable.getItem(key(notificationId.toString())) ?: throw NotificationNotFoundException(notificationId)

    /**
     * Get all Notifications by sourceReference and sourceType for one of the specified gssCodes.
     */
    fun getBySourceReference(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<Notification> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .build()

        val index = notificationsTable.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Get all Notification Summaries by sourceReference and sourceType for one of the specified gssCodes.
     */
    fun getNotificationSummariesBySourceReference(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<NotificationSummary> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .build()

        val index = notificationsSummaryTable.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Remove all Notifications by sourceReference and sourceType for the specified gssCode.
     */
    fun removeBySourceReference(sourceReference: String, sourceType: SourceType, gssCode: String) {
        with(getBySourceReference(sourceReference, sourceType, listOf(gssCode))) {
            logger.debug("Removing [$size] notifications")
            forEach { notification -> notificationsTable.deleteItem(notification) }
        }
    }

    private fun queryRequest(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): QueryEnhancedRequest.Builder =
        QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(key(sourceReference)))
            .filterExpression(sourceTypeAndGssCodesFilterExpression(sourceType, gssCodes))

    private fun sourceTypeAndGssCodesFilterExpression(sourceType: SourceType, gssCodes: List<String>): Expression =
        Expression.builder()
            .expression("#sourceType = :sourceType AND #gssCode IN (:gssCodes)")
            .putExpressionName("#sourceType", "sourceType")
            .putExpressionValue(":sourceType", AttributeValue.fromS(sourceType.name))
            .putExpressionName("#gssCode", "gssCode")
            .putExpressionValue(":gssCodes", AttributeValue.fromS(gssCodes.joinToString(",")))
            .build()

    private fun key(partitionValue: String): Key =
        Key.builder().partitionValue(partitionValue).build()
}
