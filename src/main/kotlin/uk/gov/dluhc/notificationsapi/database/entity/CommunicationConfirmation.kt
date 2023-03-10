package uk.gov.dluhc.notificationsapi.database.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import java.time.LocalDateTime
import java.util.UUID

const val COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME = "CommunicationConfirmationSourceReferenceIndex"

/**
 * DynamoDB entity that records that the ERO has contacted the anonymous elector "offline"
 * (for example by email or post) after they have rejected a photo, document or the application itself.
 * A new record of the confirmation is created each time.
 */
@DynamoDbBean
data class CommunicationConfirmation(
    @get:DynamoDbPartitionKey
    var id: UUID? = null,
    @get:DynamoDbSecondaryPartitionKey(indexNames = [COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME])
    var sourceReference: String? = null,
    var gssCode: String? = null,
    var sourceType: SourceType? = null,
    var reason: CommunicationConfirmationReason? = null,
    var channel: CommunicationConfirmationChannel? = null,
    var requestor: String? = null,
    var sentAt: LocalDateTime? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CommunicationConfirmation

        return id != null && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id)"
    }
}

enum class CommunicationConfirmationReason {
    APPLICATION_REJECTED,
    PHOTO_REJECTED,
    DOCUMENT_REJECTED,
}

enum class CommunicationConfirmationChannel {
    EMAIL,
    LETTER,
    TELEPHONE,
}
