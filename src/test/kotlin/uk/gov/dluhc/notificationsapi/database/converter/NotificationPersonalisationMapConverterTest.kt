package uk.gov.dluhc.notificationsapi.database.converter

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.util.ReflectionTestUtils.setField
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType.M
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@ExtendWith(MockitoExtension::class)
class NotificationPersonalisationMapConverterTest {
    private var converter: NotificationPersonalisationMapConverter = NotificationPersonalisationMapConverter()

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        setField(converter, "objectMapper", objectMapper)
    }

    @Nested
    inner class TransformFrom {
        @Test
        fun `should transform from Map to AttributeValue`() {
            // Given
            val rejectionReasons = listOf("reason1", "reason2")
            val serializedRejectionReasons = "[\"reason1\", \"reason2\"]"
            val name = "Fred"
            val input = mapOf(
                "name" to name,
                "rejectionReasons" to rejectionReasons
            )
            given(objectMapper.writeValueAsString(any())).willReturn(serializedRejectionReasons)

            // When
            val actual = converter.transformFrom(input)

            // Then
            assertThat(actual).isNotNull
            assertThat(actual.hasM()).isTrue
            assertThat(actual.m().getValue("name").s()).isEqualTo(name)
            assertThat(actual.m().getValue("rejectionReasons").s()).isEqualTo(serializedRejectionReasons)
            verify(objectMapper).writeValueAsString(rejectionReasons)
            verifyNoMoreInteractions(objectMapper)
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
            given(objectMapper.readValue(anyString(), any<Class<Any>>())).willReturn(rejectionReasons)

            // When
            val actual = converter.transformTo(input)

            // Then
            assertThat(actual).isNotNull
            assertThat(actual!!["name"]).isEqualTo(name)
            assertThat(actual["rejectionReasonList"]).isEqualTo(rejectionReasons)
            verify(objectMapper).readValue(serializedRejectionReasons, List::class.java)
            verifyNoMoreInteractions(objectMapper)
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
