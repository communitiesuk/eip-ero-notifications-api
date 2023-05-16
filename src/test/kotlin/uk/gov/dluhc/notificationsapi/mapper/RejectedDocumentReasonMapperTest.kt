package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi

class RejectedDocumentReasonMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = RejectedDocumentReasonMapper(messageSource)

    @Nested
    inner class ToDocumentRejectionReasonStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "DOCUMENT_MINUS_TOO_MINUS_OLD, The document is too old",
                "UNREADABLE_MINUS_DOCUMENT, The document is unable to be read",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, The document type is not valid",
                "DUPLICATE_MINUS_DOCUMENT, The document is a duplicate",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, The country for the document is invalid",
                "DOCUMENT_MINUS_ILLEGIBLE, The document does not show the applicant details clearly",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: DocumentRejectionReasonApi,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.fromApiToString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToDocumentRejectionReasonStringFromMessagingEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "DOCUMENT_MINUS_TOO_MINUS_OLD, The document is too old",
                "UNREADABLE_MINUS_DOCUMENT, The document is unable to be read",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, The document type is not valid",
                "DUPLICATE_MINUS_DOCUMENT, The document is a duplicate",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, The country for the document is invalid",
                "DOCUMENT_MINUS_ILLEGIBLE, The document does not show the applicant details clearly",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: DocumentRejectionReasonMessaging,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.fromMessagingToString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
