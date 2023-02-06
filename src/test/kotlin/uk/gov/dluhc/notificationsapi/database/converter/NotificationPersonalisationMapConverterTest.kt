package uk.gov.dluhc.notificationsapi.database.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType.M
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@ExtendWith(MockitoExtension::class)
class NotificationPersonalisationMapConverterTest {
    @InjectMocks
    private lateinit var converter: NotificationPersonalisationMapConverter

    @Nested
    inner class TransformFrom {
        @Test
        fun `should transform from Map to AttributeValue`() {
            // Given
            val rejectionReasons = listOf("reason1", "reason2")
            val serializedRejectionReasons = "[\"reason1\",\"reason2\"]"
            val name = "Fred"
            val input = mapOf(
                "name" to name,
                "rejectionReasons" to rejectionReasons
            )

            // When
            val actual = converter.transformFrom(input)

            // Then
            assertThat(actual).isNotNull
            assertThat(actual.hasM()).isTrue
            assertThat(actual.m().getValue("name").s()).isEqualTo(name)
            assertThat(actual.m().getValue("rejectionReasons").s()).isEqualTo(serializedRejectionReasons)
        }
    }

    @Nested
    inner class TransformTo {
        @Test
        fun `should transform to Map from AttributeValue`() {
            // Given
            val rejectionReasons = listOf("reason1", "reason2")
            val serializedRejectionReasons = "[\"reason1\", \"reason2\"]"
            val name = "Fred"
            val input = AttributeValue.builder().m(
                mapOf(
                    "name" to AttributeValue.builder().s(name).build(),
                    "rejectionReasonList" to AttributeValue.builder().s(serializedRejectionReasons).build()
                )
            ).build()

            // When
            val actual = converter.transformTo(input)

            // Then
            assertThat(actual).isNotNull
            assertThat(actual!!["name"]).isEqualTo(name)
            assertThat(actual["rejectionReasonList"]).isEqualTo(rejectionReasons)
        }
    }

    @Nested
    inner class Type {
        @Test
        fun `should return map type`() {
            // Given
            // When
            val actual = converter.type()

            // Then
            assertThat(actual).isNotNull
            assertThat(actual.rawClass()).isEqualTo(Map::class.java)
            assertThat(actual.rawClassParameters()).hasSize(2)
            assertThat(actual.rawClassParameters()[0].equals(EnhancedType.of(String::class.java))).isTrue
            assertThat(actual.rawClassParameters()[1].equals(EnhancedType.of(Any::class.java))).isTrue
        }
    }

    @Nested
    inner class AttributeValueType {
        @Test
        fun `should return map AttributeValueType`() {
            // Given
            // When
            val actual = converter.attributeValueType()

            // Then
            assertThat(actual).isEqualTo(M)
        }
    }
}
