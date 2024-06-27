package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DeadlineMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = DeadlineMapper(messageSource)

    @ParameterizedTest
    @CsvSource(
            value = [
                "ENGLISH, 17:00, You must do this by 17:00 on 26 June 2024 or your source application may be rejected",
                "ENGLISH, null, You must do this by 26 June 2024 or your source application may be rejected",
                "WELSH, 17:00, Rhaid i chi wneud hyn erbyn 17:00 amser y DU ar 26 Mehefin 2024 neu gall eich cais am source gael ei wrthod",
                "WELSH, null, Rhaid i chi wneud hyn erbyn 26 Mehefin 2024 neu gall eich cais am source gael ei wrthod",
            ]
    )
    fun `should map deadline string`(
            languageDto: LanguageDto,
            deadlineTime: String?,
            expectedOutcome: String
    ) {
        // Given
        val deadlineDate = LocalDate.of(2024, 6, 26)
        val deadlineTimeActual: String? = deadlineTime.takeIf { it != "null" }
        val sourceTypeString = "source"

        // When
        val actual = mapper.toDeadlineString(deadlineDate, deadlineTimeActual, languageDto, sourceTypeString)

        // Then
        assertThat(actual).isEqualTo(expectedOutcome)
    }
}
