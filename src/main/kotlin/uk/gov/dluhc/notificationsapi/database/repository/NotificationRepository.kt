package uk.gov.dluhc.notificationsapi.database.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
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
        return table.getItem(key)
    }

    fun getBySourceReference(gssCode: String, sourceType: SourceType, sourceReference: String): Notification {
        return table.scan().items()
            .first {
                notification ->
                notification.gssCode == gssCode &&
                    notification.sourceType == sourceType &&
                    notification.sourceReference == sourceReference
            }
    }
}
