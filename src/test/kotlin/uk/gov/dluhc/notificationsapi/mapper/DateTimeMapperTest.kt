package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC

class DateTimeMapperTest {
    private val mapper = DateTimeMapper()

    @Test
    fun `should map a LocalDateTime value to OffsetDateTime at UTC`() {
        // Given
        val localDateTime = LocalDateTime.of(2022, 4, 1, 9, 15, 30)
        val expectedOffsetDateTime = OffsetDateTime.of(localDateTime, UTC)

        // When
        val actual = mapper.toUtcOffset(localDateTime)

        // Then
        assertThat(actual).isEqualTo(expectedOffsetDateTime)
    }

    @Test
    fun `should return null when the parameter is null`() {
        // Given

        // When
        val actual = mapper.toUtcOffset(null)

        // Then
        assertThat(actual).isNull()
    }
}
