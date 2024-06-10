package uk.gov.dluhc.notificationsapi.database.converter

import com.fasterxml.jackson.databind.ObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
 * This class is for converting the `Map<String, Any>` `personalisation` field in the `Notification` DynamoDb entity
 * to `Map<String, String>` when being persisted. This is done by serializing any values other than `String` in the map.
 * Conversely, when the value is read from DynamoDb, it will convert it back to `Map<String, Any>`.
 *
 * This is needed, because `Any` data type is not supported natively by the DynamoDb.
 *
 * For converting back to `Map<String, Any>` there is a map `nonStringFieldTypes` defining which key values
 * will be mapped to a type other than a `String`. If a key in the `personalisation` map is not in the
 * `nonStringFieldTypes` map, it is assumed that its value is a `String` and will be converted back accordingly.
 * Otherwise, it will be deserialized to the type defined in the `nonStringFieldTypes` map.
 *
 * It is important to keep the `nonStringFieldTypes` map up to date with the `personalisation` fields
 * by adding any future non-string fields used in the notification templates. This is needed to accurately
 * convert back values to their original types when read from the DynamoDb.
 */
class NotificationPersonalisationMapConverter : AttributeConverter<Map<String, Any>> {
    private val objectMapper: ObjectMapper = ObjectMapper()
    private val nonStringFieldTypes = mapOf("rejectionReasonList" to List::class.java)

    override fun transformFrom(input: Map<String, Any>?): AttributeValue {
        return AttributeValue.builder().m(
            input?.mapValues {
                if (it.value is String) {
                    toAttributeValue(it.value as String)
                } else {
                    toAttributeValue(objectMapper.writeValueAsString(it.value))
                }
            },
        ).build()
    }

    override fun transformTo(input: AttributeValue?): Map<String, Any>? {
        return input?.m()?.entries?.associate {
            it.key to (
                if (nonStringFieldTypes.contains(it.key)) {
                    objectMapper.readValue(it.value.s(), nonStringFieldTypes[it.key])
                } else {
                    it.value.s()
                }
                )
        }
    }

    override fun type(): EnhancedType<Map<String, Any>> {
        return EnhancedType.mapOf(String::class.java, Any::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.M
    }

    private fun toAttributeValue(value: String): AttributeValue = AttributeValue.builder().s(value).build()
}
