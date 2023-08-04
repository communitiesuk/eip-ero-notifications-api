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
                "TOO_MINUS_SMALL_MINUS_OR_MINUS_UNREADABLE, The signature was too small or unreadable",
                "TOO_MINUS_LARGE, The signature was too large",
                "UNCLEAR_MINUS_FOR_MINUS_SCANNING, The signature was not clear for scanning",
                "IMAGE_MINUS_NOT_MINUS_CLEAR, The image was not clear",
                "CONTRAST_MINUS_TOO_MINUS_LOW, The contrast was too low",
                "ON_MINUS_LINED_MINUS_OR_MINUS_PATTERNED_MINUS_PAPER, The signature was on lined or patterned paper",
                "ON_MINUS_COLOURED_MINUS_PAPER, The signature was on coloured paper",
                "OBSCURED_MINUS_BY_MINUS_SHADOW, The signature was obscured by shadow",
                "PARTIALLY_MINUS_CUT_MINUS_OFF, The signature was partially cut off"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: SignatureRejectionReason,
            expected: String
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
                "TOO_MINUS_SMALL_MINUS_OR_MINUS_UNREADABLE, The signature was too small or unreadable",
                "TOO_MINUS_LARGE, The signature was too large",
                "UNCLEAR_MINUS_FOR_MINUS_SCANNING, The signature was not clear for scanning",
                "IMAGE_MINUS_NOT_MINUS_CLEAR, The image was not clear",
                "CONTRAST_MINUS_TOO_MINUS_LOW, The contrast was too low",
                "ON_MINUS_LINED_MINUS_OR_MINUS_PATTERNED_MINUS_PAPER, The signature was on lined or patterned paper",
                "ON_MINUS_COLOURED_MINUS_PAPER, The signature was on coloured paper",
                "OBSCURED_MINUS_BY_MINUS_SHADOW, The signature was obscured by shadow",
                "PARTIALLY_MINUS_CUT_MINUS_OFF, The signature was partially cut off"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: SignatureRejectionReason,
            expected: String
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
                "TOO_MINUS_SMALL_MINUS_OR_MINUS_UNREADABLE, The signature was too small or unreadable",
                "TOO_MINUS_LARGE, The signature was too large",
                "UNCLEAR_MINUS_FOR_MINUS_SCANNING, The signature was not clear for scanning",
                "IMAGE_MINUS_NOT_MINUS_CLEAR, The image was not clear",
                "CONTRAST_MINUS_TOO_MINUS_LOW, The contrast was too low",
                "ON_MINUS_LINED_MINUS_OR_MINUS_PATTERNED_MINUS_PAPER, The signature was on lined or patterned paper",
                "ON_MINUS_COLOURED_MINUS_PAPER, The signature was on coloured paper",
                "OBSCURED_MINUS_BY_MINUS_SHADOW, The signature was obscured by shadow",
                "PARTIALLY_MINUS_CUT_MINUS_OFF, The signature was partially cut off"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason,
            expected: String
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
                "TOO_MINUS_SMALL_MINUS_OR_MINUS_UNREADABLE, The signature was too small or unreadable",
                "TOO_MINUS_LARGE, The signature was too large",
                "UNCLEAR_MINUS_FOR_MINUS_SCANNING, The signature was not clear for scanning",
                "IMAGE_MINUS_NOT_MINUS_CLEAR, The image was not clear",
                "CONTRAST_MINUS_TOO_MINUS_LOW, The contrast was too low",
                "ON_MINUS_LINED_MINUS_OR_MINUS_PATTERNED_MINUS_PAPER, The signature was on lined or patterned paper",
                "ON_MINUS_COLOURED_MINUS_PAPER, The signature was on coloured paper",
                "OBSCURED_MINUS_BY_MINUS_SHADOW, The signature was obscured by shadow",
                "PARTIALLY_MINUS_CUT_MINUS_OFF, The signature was partially cut off"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toSignatureRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }
}
