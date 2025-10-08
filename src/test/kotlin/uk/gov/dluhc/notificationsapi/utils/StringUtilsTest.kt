package uk.gov.dluhc.notificationsapi.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

class StringUtilsTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = ["    ", "\n"])
    fun `should convert blank or null strings to empty string`(
        originalString: String?,
    ) {
        // Given
        // When
        val actual = originalString.getSafeValue()

        // Then
        assertThat(actual).isEqualTo("")
    }

    @Test
    fun `should not convert non-empty strings`() {
        // Given
        // When
        val actual = "originalString".getSafeValue()

        // Then
        assertThat(actual).isEqualTo("originalString")
    }
}
