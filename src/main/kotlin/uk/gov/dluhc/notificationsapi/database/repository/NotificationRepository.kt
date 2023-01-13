package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
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

    fun getNotification(notificationId: UUID, gssCode: String): Notification {
        try {
            return table.getItem(key(notificationId.toString(), gssCode))
        } catch (ex: NullPointerException) {
            throw NotificationNotFoundException(notificationId, gssCode)
        }
    }

    fun getBySourceReference(sourceReference: String, gssCode: String): List<Notification> {
        val queryConditional = QueryConditional.keyEqualTo(key(sourceReference, gssCode))
        val index = table.index(SOURCE_REFERENCE_INDEX_NAME)
        val query = QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
        return index.query(query).flatMap { it.items() }
    }

    /**
     * Get all Notifications by sourceReference, gssCode and sourceType.
     *
     * The returned items are a projection containing just the attributes id, type, channel, requestor and sentAt.
     * The remaining fields in the Notifications will be null.
     */
    fun getBySourceReferenceAndSourceType(
        sourceReference: String,
        gssCode: String,
        sourceType: SourceType,
    ): List<Notification> {
        val queryConditional = QueryConditional.keyEqualTo(key(sourceReference, gssCode))
        val filterExpression = Expression.builder()
            .expression("#sourceType = :sourceType").putExpressionName("#sourceType", "sourceType")
            .putExpressionValue(":sourceType", AttributeValues.stringValue(sourceType.name))
            .build()
        val query = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .filterExpression(filterExpression)
            .attributesToProject(PROJECTION_ATTRIBUTES)
            .build()

        val index = table.index(SOURCE_REFERENCE_INDEX_NAME)
        return index.query(query).flatMap { it.items() }
    }

    fun removeBySourceReference(sourceReference: String, gssCode: String) {
        with(getBySourceReference(sourceReference, gssCode)) {
            logger.debug("Removing [$size] notifications")
            forEach { notification -> table.deleteItem(notification) }
        }
    }

    private fun key(partitionValue: String, sortValue: String): Key =
        Key.builder().partitionValue(partitionValue).sortValue(sortValue).build()
}
