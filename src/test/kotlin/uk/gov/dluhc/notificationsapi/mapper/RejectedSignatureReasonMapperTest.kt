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
                "PARTIALLY_CUT_OFF,Some of the signature is missing",
                "TOO_DARK,The image is too dark",
                "NOT_IN_FOCUS,Signature is not clear or in focus (low quality or pixelated)",
                "HAS_SHADOWS,The signature has shadows over it",
                "WRONG_SIZE,Image is either too big or too small",
                "PAPER_NOT_PLAIN,'Paper is not plain white (has lines, squares, patterns or creases)'",
                "FADED,'Signature is faded, or not in black ink'",
                "SHADOWS_SMUDGES_OR_MARKS,'Image is unclear due to shadows, smudges or marks (or is too dark)'",
                "NOT_HANDWRITTEN,Signature is not handwritten",
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
                "PARTIALLY_CUT_OFF,Mae rhywfaint o'r llofnod ar goll",
                "TOO_DARK,Mae'r llun yn rhy dywyll",
                "NOT_IN_FOCUS,Nid yw'r llofnod yn glir neu mewn ffocws (ansawdd isel neu wedi'i bicselu)",
                "HAS_SHADOWS,Mae cysgodion dros y llofnod",
                "WRONG_SIZE,Mae'r llun naill ai'n rhy fawr neu'n rhy fach",
                "PAPER_NOT_PLAIN,'Nid yw''r papur yn bapur gwyn plaen (mae llinellau, sgwariau, patrymau neu grychau arno)'",
                "FADED,'Mae''r llofnod wedi colli ei liw, neu nid yw mewn inc du'",
                "SHADOWS_SMUDGES_OR_MARKS,'Nid yw''r llun yn glir gan fod cysgodion, olion neu farciau arno (neu mae''n rhy dywyll)'",
                "NOT_HANDWRITTEN,'Nid yw''r llofnod wedi''i wneud â llaw'",
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
                "PARTIALLY_CUT_OFF,Some of the signature is missing",
                "TOO_DARK,The image is too dark",
                "NOT_IN_FOCUS,Signature is not clear or in focus (low quality or pixelated)",
                "HAS_SHADOWS,The signature has shadows over it",
                "WRONG_SIZE,Image is either too big or too small",
                "PAPER_NOT_PLAIN,'Paper is not plain white (has lines, squares, patterns or creases)'",
                "FADED,'Signature is faded, or not in black ink'",
                "SHADOWS_SMUDGES_OR_MARKS,'Image is unclear due to shadows, smudges or marks (or is too dark)'",
                "NOT_HANDWRITTEN,Signature is not handwritten",
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
                "PARTIALLY_CUT_OFF,Mae rhywfaint o'r llofnod ar goll",
                "TOO_DARK,Mae'r llun yn rhy dywyll",
                "NOT_IN_FOCUS,Nid yw'r llofnod yn glir neu mewn ffocws (ansawdd isel neu wedi'i bicselu)",
                "HAS_SHADOWS,Mae cysgodion dros y llofnod",
                "WRONG_SIZE,Mae'r llun naill ai'n rhy fawr neu'n rhy fach",
                "PAPER_NOT_PLAIN,'Nid yw''r papur yn bapur gwyn plaen (mae llinellau, sgwariau, patrymau neu grychau arno)'",
                "FADED,'Mae''r llofnod wedi colli ei liw, neu nid yw mewn inc du'",
                "SHADOWS_SMUDGES_OR_MARKS,'Nid yw''r llun yn glir gan fod cysgodion, olion neu farciau arno (neu mae''n rhy dywyll)'",
                "NOT_HANDWRITTEN,'Nid yw''r llofnod wedi''i wneud â llaw'",
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
            "ENGLISH,true,The signature you provided in your text application is not suitable.",
            "WELSH,true,Nid yw'r llofnod y gwnaethoch ei ddarparu yn eich cais am text yn addas.",
            "ENGLISH,false,null",
        ],
        nullValues = ["null"],
    )
    fun `should map source type to human readable messages`(
        language: LanguageDto,
        includeText: Boolean,
        expected: String?,
    ) {
        // Given

        // When
        val actual = mapper.toSignatureNotSuitableText("text", language, includeText)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
