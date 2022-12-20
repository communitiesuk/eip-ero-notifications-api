package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.Language as MessageLanguageEnum

class LanguageMapperTest {

    private val mapper = LanguageMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "CY, WELSH",
            "EN, ENGLISH",
        ]
    )
    fun `should map api language to language dto`(
        apiLanguageEnum: Language,
        expected: LanguageDto
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiLanguageEnum)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "CY, WELSH",
            "EN, ENGLISH",
        ]
    )
    fun `should map message language to language dto`(
        messageLanguageEnum: MessageLanguageEnum,
        expected: LanguageDto
    ) {
        // Given

        // When
        val actual = mapper.fromMessageToDto(messageLanguageEnum)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
