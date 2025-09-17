package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason

class RejectedSignatureReasonMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = SignatureRejectionReasonMapper(messageSource)

    @Nested
    inner class ToSignatureRejectionReasonStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "PARTIALLY_MINUS_CUT_MINUS_OFF,The image has some of it cut off",
                "TOO_MINUS_DARK,The image is too dark",
                "NOT_MINUS_IN_MINUS_FOCUS,The signature is not in focus",
                "HAS_MINUS_SHADOWS,The signature has shadows over it",
                "WRONG_MINUS_SIZE,The image is either too big or too small",
            ],
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: SignatureRejectionReason,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toSignatureRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            Assertions.assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "PARTIALLY_MINUS_CUT_MINUS_OFF,Mae darn o'r llun wedi'i dorri i ffwrdd",
                "TOO_MINUS_DARK,Mae'r llun yn rhy dywyll",
                "NOT_MINUS_IN_MINUS_FOCUS,Nid yw'r llofnod mewn ffocws",
                "HAS_MINUS_SHADOWS,Mae cysgodion dros y llofnod",
                "WRONG_MINUS_SIZE,Mae'r llun naill ai'n rhy fawr neu'n rhy fach",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: SignatureRejectionReason,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toSignatureRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToSignatureRejectionReasonStringFromMessagingEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "PARTIALLY_MINUS_CUT_MINUS_OFF,The image has some of it cut off",
                "TOO_MINUS_DARK,The image is too dark",
                "NOT_MINUS_IN_MINUS_FOCUS,The signature is not in focus",
                "HAS_MINUS_SHADOWS,The signature has shadows over it",
                "WRONG_MINUS_SIZE,The image is either too big or too small",
            ],
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toSignatureRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            Assertions.assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "PARTIALLY_MINUS_CUT_MINUS_OFF,Mae darn o'r llun wedi'i dorri i ffwrdd",
                "TOO_MINUS_DARK,Mae'r llun yn rhy dywyll",
                "NOT_MINUS_IN_MINUS_FOCUS,Nid yw'r llofnod mewn ffocws",
                "HAS_MINUS_SHADOWS,Mae cysgodion dros y llofnod",
                "WRONG_MINUS_SIZE,Mae'r llun naill ai'n rhy fawr neu'n rhy fach",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toSignatureRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "ENGLISH,The signature you provided in your text vote application is not suitable.",
            "WELSH,Nid yw'r llofnod y gwnaethoch ei ddarparu yn eich cais am bleidlais text yn addas.",
        ],
    )
    fun `should map source type to human readable messages`(
        language: LanguageDto,
        expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toSignatureNotSuitableText("text", language)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
