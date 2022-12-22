package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.NotificationNotFoundException
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.Notification.Companion.SOURCE_REFERENCE_INDEX_NAME
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Repository
class NotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val tableSchema = TableSchema.fromBean(Notification::class.java)
    }

    private val table = client.table(tableConfig.notificationsTableName, tableSchema)

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

    private fun key(partitionValue: String, sortValue: String): Key =
        Key.builder().partitionValue(partitionValue).sortValue(sortValue).build()
}
