package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessagingEnum
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApiEnum

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
                "MARRIAGE_MINUS_OR_MINUS_CIVIL_MINUS_PARTNERSHIP_MINUS_CERTIFICATE, Marriage or civil partnership certificate",
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
                "PASSPORT, Passport",
                "EEA_MINUS_ISSUED_MINUS_ID_MINUS_CARD, EEA issued ID card",
                "PHOTOCARD_MINUS_DRIVING_MINUS_LICENCE, Photo card driving licence",
                "BIOMETRIC_MINUS_IDENTITY_MINUS_DOCUMENT, Biometric identity document",
                "NI_MINUS_ELECTORAL_MINUS_IDENTITY_MINUS_CARD, NI electoral identity card",
                "CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Driving licence granted by a Crown Dependency",
            ],
        )
        fun `should map enums to human readable messages in English`(
            documentType: DocumentTypeApiEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toDocumentTypeString(documentType, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "BIRTH_MINUS_CERTIFICATE, Tystysgrif geni",
                "MARRIAGE_MINUS_OR_MINUS_CIVIL_MINUS_PARTNERSHIP_MINUS_CERTIFICATE, Tystysgrif priodas neu bartneriaeth sifil",
                "ADOPTION_MINUS_CERTIFICATE, Tystysgrif mabwysiadu",
                "FIREARMS_MINUS_CERTIFICATE, Tystysgrif dryll",
                "UK_MINUS_CD_MINUS_ISSUED_MINUS_NON_MINUS_PHOTO_MINUS_CARD_MINUS_DRIVING_MINUS_LICENCE, Fersiwn papur hen arddull o drwydded yrru gyfredol",
                "NON_MINUS_UK_MINUS_CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru ffotograffig gyfredol",
                "POLICE_MINUS_BAIL_MINUS_SHEET, Taflen mechnïaeth yr heddlu",
                "MORTGAGE_MINUS_STATEMENT, Datganiad morgais",
                "BANK_MINUS_OR_MINUS_BUILDING_MINUS_SOCIETY_MINUS_STATEMENT_MINUS_OR_MINUS_CONFIRMATION_MINUS_LETTER, Datganiad banc neu gymdeithas adeiladu neu lythyr cadarnhad",
                "CREDIT_MINUS_CARD_MINUS_STATEMENT, Datganiad cerdyn credyd",
                "PENSION_MINUS_STATEMENT, Datganiad pensiwn",
                "COUNCIL_MINUS_TAX_MINUS_STATEMENT_MINUS_OR_MINUS_DEMAND_MINUS_LETTER, Datganiad treth gyngor neu lythyr hawlio",
                "UTILITY_MINUS_BILL, Bil cyfleustodau",
                "P45_MINUS_OR_MINUS_P60_MINUS_FORM, Ffurflen P45 neu P60",
                "STATEMENT_MINUS_OF_MINUS_OR_MINUS_ENTITLEMENT_MINUS_TO_MINUS_BENEFITS, Datganiad neu hawl i fudd-daliadau",
                "PASSPORT, Pasbort",
                "EEA_MINUS_ISSUED_MINUS_ID_MINUS_CARD, Cerdyn adnabod a gyhoeddwyd gan yr AEE",
                "PHOTOCARD_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru cerdyn llun",
                "BIOMETRIC_MINUS_IDENTITY_MINUS_DOCUMENT, Dogfen hunaniaeth fiometrig",
                "NI_MINUS_ELECTORAL_MINUS_IDENTITY_MINUS_CARD, Cerdyn adnabod etholiadol YG",
                "CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru a roddwyd gan Dibyniaeth y Goron",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            documentType: DocumentTypeApiEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toDocumentTypeString(documentType, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToDocumentTypeStringFromMessagingEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "BIRTH_MINUS_CERTIFICATE, Birth certificate",
                "MARRIAGE_MINUS_OR_MINUS_CIVIL_MINUS_PARTNERSHIP_MINUS_CERTIFICATE, Marriage or civil partnership certificate",
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
                "PASSPORT, Passport",
                "EEA_MINUS_ISSUED_MINUS_ID_MINUS_CARD, EEA issued ID card",
                "PHOTOCARD_MINUS_DRIVING_MINUS_LICENCE, Photo card driving licence",
                "BIOMETRIC_MINUS_IDENTITY_MINUS_DOCUMENT, Biometric identity document",
                "NI_MINUS_ELECTORAL_MINUS_IDENTITY_MINUS_CARD, NI electoral identity card",
                "CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Driving licence granted by a Crown Dependency",
            ],
        )
        fun `should map enums to human readable messages in English`(
            documentType: DocumentTypeMessagingEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toDocumentTypeString(documentType, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "BIRTH_MINUS_CERTIFICATE, Tystysgrif geni",
                "MARRIAGE_MINUS_OR_MINUS_CIVIL_MINUS_PARTNERSHIP_MINUS_CERTIFICATE, Tystysgrif priodas neu bartneriaeth sifil",
                "ADOPTION_MINUS_CERTIFICATE, Tystysgrif mabwysiadu",
                "FIREARMS_MINUS_CERTIFICATE, Tystysgrif dryll",
                "UK_MINUS_CD_MINUS_ISSUED_MINUS_NON_MINUS_PHOTO_MINUS_CARD_MINUS_DRIVING_MINUS_LICENCE, Fersiwn papur hen arddull o drwydded yrru gyfredol",
                "NON_MINUS_UK_MINUS_CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru ffotograffig gyfredol",
                "POLICE_MINUS_BAIL_MINUS_SHEET, Taflen mechnïaeth yr heddlu",
                "MORTGAGE_MINUS_STATEMENT, Datganiad morgais",
                "BANK_MINUS_OR_MINUS_BUILDING_MINUS_SOCIETY_MINUS_STATEMENT_MINUS_OR_MINUS_CONFIRMATION_MINUS_LETTER, Datganiad banc neu gymdeithas adeiladu neu lythyr cadarnhad",
                "CREDIT_MINUS_CARD_MINUS_STATEMENT, Datganiad cerdyn credyd",
                "PENSION_MINUS_STATEMENT, Datganiad pensiwn",
                "COUNCIL_MINUS_TAX_MINUS_STATEMENT_MINUS_OR_MINUS_DEMAND_MINUS_LETTER, Datganiad treth gyngor neu lythyr hawlio",
                "UTILITY_MINUS_BILL, Bil cyfleustodau",
                "P45_MINUS_OR_MINUS_P60_MINUS_FORM, Ffurflen P45 neu P60",
                "STATEMENT_MINUS_OF_MINUS_OR_MINUS_ENTITLEMENT_MINUS_TO_MINUS_BENEFITS, Datganiad neu hawl i fudd-daliadau",
                "PASSPORT, Pasbort",
                "EEA_MINUS_ISSUED_MINUS_ID_MINUS_CARD, Cerdyn adnabod a gyhoeddwyd gan yr AEE",
                "PHOTOCARD_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru cerdyn llun",
                "BIOMETRIC_MINUS_IDENTITY_MINUS_DOCUMENT, Dogfen hunaniaeth fiometrig",
                "NI_MINUS_ELECTORAL_MINUS_IDENTITY_MINUS_CARD, Cerdyn adnabod etholiadol YG",
                "CD_MINUS_ISSUED_MINUS_DRIVING_MINUS_LICENCE, Trwydded yrru a roddwyd gan Dibyniaeth y Goron",
            ],
        )
        fun `should map enums to human readable messages in Welsh`(
            documentType: DocumentTypeMessagingEnum,
            expected: String,
        ) {
            // Given

            // When
            val actual = mapper.toDocumentTypeString(documentType, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
