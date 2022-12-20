package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language

class LanguageMapperTest {

    private val mapper = LanguageMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "CY, WELSH",
            "EN, ENGLISH",
        ]
    )
    fun `should map to language dto`(
        request: Language,
        expected: LanguageDto
    ) {
        // Given

        // When
        val actual = mapper.toDto(request)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
