package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.entity.EmailNotification

private val logger = KotlinLogging.logger {}

@Repository
class EmailNotificationRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val tableSchema = TableSchema.fromBean(EmailNotification::class.java)
    }

    private val table = client.table(tableConfig.notificationsTableName, tableSchema)

    fun saveEmailNotification(emailNotification: EmailNotification) {
        try {
            table.putItem(emailNotification)
        } catch (error: SdkClientException) {
            logger.error { "Client error attempting to save 'sent email notification': $error" }
        } catch (error: SdkServiceException) {
            logger.error { "Service error attempting to save 'sent email notification': $error" }
        }
    }

    fun getEmailNotification(notificationId: String): EmailNotification {
        val key = Key.builder().partitionValue(notificationId).build()
        return table.getItem(key)
    }
}
