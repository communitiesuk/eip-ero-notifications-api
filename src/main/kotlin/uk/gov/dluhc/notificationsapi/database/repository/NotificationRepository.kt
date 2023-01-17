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
import uk.gov.dluhc.notificationsapi.database.entity.Notification.Companion.SOURCE_REFERENCE_INDEX_NAME
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Repository
class NotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val TABLE_SCHEMA = TableSchema.fromBean(Notification::class.java)
        private val PROJECTION_ATTRIBUTES = listOf("id", "type", "channel", "requestor", "sentAt")
    }

    private val table = client.table(tableConfig.notificationsTableName, TABLE_SCHEMA)

    fun saveNotification(notification: Notification) {
        logger.debug("Saving notification for type [${notification.type}], channel: [${notification.channel}]")
        table.putItem(notification)
    }

    /**
     * Returns the Notification identified by its id (primary partition key)
     */
    fun getNotification(notificationId: UUID): Notification =
        table.getItem(key(notificationId.toString())) ?: throw NotificationNotFoundException(notificationId)

    /**
     * Get all Notifications by sourceReference and sourceType for one of the specified gssCodes.
     */
    fun getBySourceReference(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<Notification> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .build()

        val index = table.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Get all Notifications by sourceReference and sourceType for one of the specified gssCodes.
     *
     * The returned items are a projection containing just the attributes id, type, channel, requestor and sentAt.
     * The remaining fields in the Notifications will be null.
     */
    fun getNotificationSummariesBySourceReference(sourceReference: String, sourceType: SourceType, gssCodes: List<String>): List<Notification> {
        val queryRequest = queryRequest(sourceReference, sourceType, gssCodes)
            .attributesToProject(PROJECTION_ATTRIBUTES)
            .build()

        val index = table.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(queryRequest).flatMap { it.items() }
    }

    /**
     * Remove all Notifications by sourceReference and sourceType for the specified gssCode.
     */
    fun removeBySourceReference(sourceReference: String, sourceType: SourceType, gssCode: String) {
        with(getBySourceReference(sourceReference, sourceType, listOf(gssCode))) {
            logger.debug("Removing [$size] notifications")
            forEach { notification -> table.deleteItem(notification) }
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
