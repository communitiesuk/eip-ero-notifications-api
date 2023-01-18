package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import java.time.LocalDateTime
import java.util.UUID

const val SOURCE_REFERENCE_INDEX_NAME = "SourceReferenceIndex"

/**
 * DynamoDB entity that maps to the main `notifications` table, and represents a Notification sent as part of an application.
 */@DynamoDbBean
data class Notification(
    @get:DynamoDbPartitionKey var id: UUID? = null,
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SOURCE_REFERENCE_INDEX_NAME]) var sourceReference: String? = null,
    var gssCode: String? = null,
    var sourceType: SourceType? = null,
    var type: NotificationType? = null,
    var channel: Channel? = null,
    var toEmail: String? = null,
    var toPostalAddress: PostalAddress? = null,
    var requestor: String? = null,
    var sentAt: LocalDateTime? = null,
    var personalisation: Map<String, String>? = null,
    var notifyDetails: NotifyDetails? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Notification

        return id != null && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id)"
    }
}
