package uk.gov.dluhc.notificationsapi.domain

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime
import java.util.UUID

@DynamoDbBean
data class EmailNotification(
    @get:DynamoDbPartitionKey var id: UUID? = null,
    @get:DynamoDbSortKey var gssCode: String? = null,
    var sourceReference: String? = null,
    var sourceType: SourceType? = null,
    var type: NotificationType? = null,
    var toEmail: String? = null,
    var requestor: String? = null,
    var sentAt: LocalDateTime? = null,
    var personalisation: Map<String, String>? = null,
    var notifyDetails: SendNotificationResponse? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EmailNotification

        return id != null && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id)"
    }
}
