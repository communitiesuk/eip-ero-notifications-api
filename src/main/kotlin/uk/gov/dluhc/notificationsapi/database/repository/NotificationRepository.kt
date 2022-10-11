package uk.gov.dluhc.notificationsapi.database.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.NotificationNotFoundException
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import java.util.UUID

@Repository
class NotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val tableSchema = TableSchema.fromBean(Notification::class.java)
    }

    private val table = client.table(tableConfig.notificationsTableName, tableSchema)

    fun saveNotification(notification: Notification) {
        table.putItem(notification)
    }

    fun getNotification(notificationId: UUID, gssCode: String): Notification {
        val key = Key.builder()
            .partitionValue(notificationId.toString())
            .sortValue(gssCode)
            .build()
        try {
            return table.getItem(key)
        } catch (ex: NullPointerException) {
            throw NotificationNotFoundException(notificationId, gssCode)
        }
    }

    fun getBySourceReference(sourceReference: String, gssCode: String): List<Notification> {
        val key = Key.builder()
            .partitionValue(sourceReference)
            .sortValue(gssCode)
            .build()
        val queryConditional = QueryConditional.keyEqualTo(key)
        val index = table.index("notificationsBySourceReference")
        val query = QueryEnhancedRequest.builder().queryConditional(queryConditional).build()
        return index.query(query).flatMap { it.items() }
        // return table.scan().items()
        //     .first {
        //         notification ->
        //         notification.gssCode == gssCode &&
        //             notification.sourceType == sourceType &&
        //             notification.sourceReference == sourceReference
        //     }
    }
}
