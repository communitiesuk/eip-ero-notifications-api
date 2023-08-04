package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import java.time.LocalDateTime
import java.util.UUID

const val NOTIFICATION_AUDIT_SOURCE_REFERENCE_INDEX_NAME = "SourceReferenceIndex"

/**
 * DynamoDB entity that maps to the main `notification audits` table, and represents the audit
 * from a notification with all the PII data removed
 */
@DynamoDbBean
data class NotificationAudit(
    @get:DynamoDbPartitionKey var id: UUID? = null,
    @get:DynamoDbSecondaryPartitionKey(indexNames = [NOTIFICATION_AUDIT_SOURCE_REFERENCE_INDEX_NAME]) var sourceReference: String? = null,
    var gssCode: String? = null,
    var sourceType: SourceType? = null,
    var channel: Channel? = null,
    var requestor: String? = null,
    var sentAt: LocalDateTime? = null,
    var templateDetails: NotificationAuditTemplateDetails? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as NotificationAudit

        return id != null && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id)"
    }
}
