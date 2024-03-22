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
                "UNREADABLE_MINUS_DOCUMENT, We were unable to read the document provided because it was not clear or not showing the information we needed",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, The document provided is not of a type that we can accept for the purposes of checking your identity",
                "DUPLICATE_MINUS_DOCUMENT, This was a duplicate of another document that you have provided",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, We are not able to accept documents from this country for the purposes of checking your identity",
                "APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR, The document needs to clearly show your name",
                "PARENT_MINUS_GUARDIAN_MINUS_NOT_MINUS_CLEAR, The document must prove your connection to your parent or guardian",
                "DETAILS_MINUS_ON_MINUS_DOCUMENT_MINUS_DO_MINUS_NOT_MINUS_MATCH, Information provided on the document does not match information from your application",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: DocumentRejectionReasonApi,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toDocumentRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "DOCUMENT_MINUS_TOO_MINUS_OLD, Mae'r ddogfen yn rhy hen",
                "UNREADABLE_MINUS_DOCUMENT, Nid oeddem yn gallu darllen y ddogfen a ddarparwyd oherwydd nad oedd yn glir neu oherwydd nad oedd yn dangos y wybodaeth yr oedd ei hangen arnom",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, Nid yw'r ddogfen a ddarparwyd o fath y gallwn ei derbyn at ddibenion gwirio pwy ydych",
                "DUPLICATE_MINUS_DOCUMENT, Roedd hwn yn gopi dyblyg o ddogfen arall a ddarparwyd gennych",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, Ni allwn dderbyn dogfennau o'r wlad hon at ddibenion gwirio pwy ydych",
                "APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR, Mae angen i'r ddogfen ddangos eich enw'n glir",
                "PARENT_MINUS_GUARDIAN_MINUS_NOT_MINUS_CLEAR, Rhaid i'r ddogfen brofi'ch cysylltiad â'ch rhiant neu warcheidwad",
                "DETAILS_MINUS_ON_MINUS_DOCUMENT_MINUS_DO_MINUS_NOT_MINUS_MATCH, Nid yw'r wybodaeth a ddarperir ar y ddogfen yn cyfateb i'r wybodaeth o'ch cais",
                "OTHER, Eraill"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: DocumentRejectionReasonApi,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toDocumentRejectionReasonString(rejectionReason, LanguageDto.WELSH)

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
                "UNREADABLE_MINUS_DOCUMENT, We were unable to read the document provided because it was not clear or not showing the information we needed",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, The document provided is not of a type that we can accept for the purposes of checking your identity",
                "DUPLICATE_MINUS_DOCUMENT, This was a duplicate of another document that you have provided",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, We are not able to accept documents from this country for the purposes of checking your identity",
                "APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR, The document needs to clearly show your name",
                "PARENT_MINUS_GUARDIAN_MINUS_NOT_MINUS_CLEAR, The document must prove your connection to your parent or guardian",
                "DETAILS_MINUS_ON_MINUS_DOCUMENT_MINUS_DO_MINUS_NOT_MINUS_MATCH, Information provided on the document does not match information from your application",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: DocumentRejectionReasonMessaging,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toDocumentRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "DOCUMENT_MINUS_TOO_MINUS_OLD, Mae'r ddogfen yn rhy hen",
                "UNREADABLE_MINUS_DOCUMENT, Nid oeddem yn gallu darllen y ddogfen a ddarparwyd oherwydd nad oedd yn glir neu oherwydd nad oedd yn dangos y wybodaeth yr oedd ei hangen arnom",
                "INVALID_MINUS_DOCUMENT_MINUS_TYPE, Nid yw'r ddogfen a ddarparwyd o fath y gallwn ei derbyn at ddibenion gwirio pwy ydych",
                "DUPLICATE_MINUS_DOCUMENT, Roedd hwn yn gopi dyblyg o ddogfen arall a ddarparwyd gennych",
                "INVALID_MINUS_DOCUMENT_MINUS_COUNTRY, Ni allwn dderbyn dogfennau o'r wlad hon at ddibenion gwirio pwy ydych",
                "APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR, Mae angen i'r ddogfen ddangos eich enw'n glir",
                "PARENT_MINUS_GUARDIAN_MINUS_NOT_MINUS_CLEAR, Rhaid i'r ddogfen brofi'ch cysylltiad â'ch rhiant neu warcheidwad",
                "DETAILS_MINUS_ON_MINUS_DOCUMENT_MINUS_DO_MINUS_NOT_MINUS_MATCH, Nid yw'r wybodaeth a ddarperir ar y ddogfen yn cyfateb i'r wybodaeth o'ch cais",
                "OTHER, Eraill"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: DocumentRejectionReasonMessaging,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toDocumentRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
