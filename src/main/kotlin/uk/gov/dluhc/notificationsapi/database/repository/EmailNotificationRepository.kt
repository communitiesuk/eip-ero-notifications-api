package uk.gov.dluhc.notificationsapi.database.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.domain.EmailNotification
import java.util.UUID

@Repository
class EmailNotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val tableSchema = TableSchema.fromBean(EmailNotification::class.java)
    }

    private val table = client.table(tableConfig.notificationsTableName, tableSchema)

    fun saveEmailNotification(emailNotification: EmailNotification) {
        table.putItem(emailNotification)
    }

    fun getEmailNotification(notificationId: UUID, gssCode: String): EmailNotification {
        val key = Key.builder()
            .partitionValue(notificationId.toString())
            .sortValue(gssCode)
            .build()
        return table.getItem(key)
    }
}
