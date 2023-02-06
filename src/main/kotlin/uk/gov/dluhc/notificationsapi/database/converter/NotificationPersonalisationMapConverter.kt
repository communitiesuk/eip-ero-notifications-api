package uk.gov.dluhc.notificationsapi.database.converter

import com.fasterxml.jackson.databind.ObjectMapper
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

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
            }
        ).build()
    }

    override fun transformTo(input: AttributeValue?): Map<String, Any>? {
        return input?.m()?.entries?.associate {
            it.key to (
                if (nonStringFieldTypes.contains(it.key))
                    objectMapper.readValue(it.value.s(), nonStringFieldTypes[it.key])
                else it.value.s()
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
