package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.DocumentType

class RejectedDocumentTypeMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = RejectedDocumentTypeMapper(messageSource)

    @Nested
    inner class ToDocumentTypeStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "BIRTH_MINUS_CERTIFICATE, Birth certificate",
                "MARRIAGE_MINUS_OR_MINUS_CIVIL_MINUS_PARTNERSHIP_MINUS_CERTIFICATE, marriage or civil partnership certificate",
                "ADOPTION_MINUS_CERTIFICATE, Adoption certificate",
                "FIREARMS_MINUS_CERTIFICATE, Firearms certificate",
                "UK_MINUS_CD_MINUS_ISSUED_MINUS_NON_MINUS_PHOTO_MINUS_CARD_MINUS_DRIVING_MINUS_LICENCE, Old style paper version of a current driving licence",
                "NON_MINUS_UK_MINUS_CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Current photo driving licence",
                "POLICE_MINUS_BAIL_MINUS_SHEET, Police bail sheet",
                "MORTGAGE_MINUS_STATEMENT, Mortgage statement",
                "BANK_MINUS_OR_MINUS_BUILDING_MINUS_SOCIETY_MINUS_STATEMENT_MINUS_OR_MINUS_CONFIRMATION_MINUS_LETTER, Bank or building society statement or confirmation letter",
                "CREDIT_MINUS_CARD_MINUS_STATEMENT, Credit card statement",
                "PENSION_MINUS_STATEMENT, Pension statement",
                "COUNCIL_MINUS_TAX_MINUS_STATEMENT_MINUS_OR_MINUS_DEMAND_MINUS_LETTER, Council tax statement or demand letter",
                "UTILITY_MINUS_BILL, Utility bill",
                "P45_MINUS_OR_MINUS_P60_MINUS_FORM, P45 or P60 form",
                "STATEMENT_MINUS_OF_MINUS_OR_MINUS_ENTITLEMENT_MINUS_TO_MINUS_BENEFITS, Statement of or entitlement to benefits",
                "COMPLETED_MINUS_ATTESTATION_MINUS_DOCUMENTS, Completed attestation documents",
                "PASSPORT, Passport",
                "EEA_MINUS_ISSUED_MINUS_ID_MINUS_CARD,EEA issued ID card",
                "PHOTOCARD_MINUS_DRIVING_MINUS_LICENCE, Photo card driving licence",
                "BIOMETRIC_MINUS_IDENTITY_MINUS_DOCUMENT, Biometric identity document",
                "NI_MINUS_ELECTORAL_MINUS_IDENTITY_MINUS_CARD, NI electoral identity card",
            ]
        )
        fun `should map enums to human readable messages in English`(
            documentType: DocumentType,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toDocumentTypeString(documentType, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
