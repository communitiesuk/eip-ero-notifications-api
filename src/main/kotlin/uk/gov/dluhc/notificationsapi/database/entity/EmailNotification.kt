package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class EmailNotification(
    @get:DynamoDbPartitionKey var id: String? = null,
    var recipent: String? = null,
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
