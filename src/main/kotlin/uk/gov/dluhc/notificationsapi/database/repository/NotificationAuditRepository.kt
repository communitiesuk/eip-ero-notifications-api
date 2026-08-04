package uk.gov.dluhc.notificationsapi.database.repository

import mu.KotlinLogging
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import uk.gov.dluhc.notificationsapi.config.DynamoDbConfiguration
import uk.gov.dluhc.notificationsapi.database.entity.NotificationAudit

private val logger = KotlinLogging.logger {}

@Repository
class NotificationAuditRepository(client: DynamoDbEnhancedClient, tableConfig: DynamoDbConfiguration) {

    companion object {
        private val NOTIFICATION_AUDITS_SCHEMA = TableSchema.fromBean(NotificationAudit::class.java)
    }

    private val notificationAuditsTable = client.table(tableConfig.notificationAuditsTableName, NOTIFICATION_AUDITS_SCHEMA)

    fun saveNotificationAudit(notificationAudit: NotificationAudit) {
        logger.debug("Saving notification audit for channel [${notificationAudit.channel}]")
        notificationAuditsTable.putItem(notificationAudit)
    }
}
