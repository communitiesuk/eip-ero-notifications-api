package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.time.LocalDateTime
import java.util.UUID

@DynamoDbBean
data class Notification(
    @get:DynamoDbPartitionKey var id: UUID? = null,
    @get:DynamoDbSortKey @get:DynamoDbSecondarySortKey(indexNames = [SOURCE_REFERENCE_INDEX_NAME]) var gssCode: String? = null,
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SOURCE_REFERENCE_INDEX_NAME]) var sourceReference: String? = null,
    var sourceType: SourceType? = null,
    var type: NotificationType? = null,
    var channel: Channel? = null,
    var toEmail: String? = null,
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

    companion object {
        const val SOURCE_REFERENCE_INDEX_NAME = "SourceReferenceIndex"
    }
}

enum class SourceType {
    VOTER_CARD,
}

enum class NotificationType {
    APPLICATION_RECEIVED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    PHOTO_RESUBMISSION,
    ID_DOCUMENT_RESUBMISSION,
}

enum class Channel {
    EMAIL,
    LETTER
}
