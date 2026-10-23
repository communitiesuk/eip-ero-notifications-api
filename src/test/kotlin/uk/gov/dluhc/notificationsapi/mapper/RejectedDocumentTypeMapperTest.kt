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
                "BIRTH_CERTIFICATE, Birth certificate",
                "MARRIAGE_OR_CIVIL_PARTNERSHIP_CERTIFICATE, Marriage or civil partnership certificate",
                "ADOPTION_CERTIFICATE, Adoption certificate",
                "FIREARMS_CERTIFICATE, Firearms certificate",
                "UK_CD_ISSUED_NON_PHOTO_CARD_DRIVING_LICENCE, Old style paper version of a current driving licence",
                "NON_UK_CD_ISSUED_DRIVING_LICENCE, Current photo driving licence",
                "POLICE_BAIL_SHEET, Police bail sheet",
                "MORTGAGE_STATEMENT, Mortgage statement",
                "BANK_OR_BUILDING_SOCIETY_STATEMENT_OR_CONFIRMATION_LETTER, Bank or building society statement or confirmation letter",
                "CREDIT_CARD_STATEMENT, Credit card statement",
                "PENSION_STATEMENT, Pension statement",
                "COUNCIL_TAX_STATEMENT_OR_DEMAND_LETTER, Council tax statement or demand letter",
                "UTILITY_BILL, Utility bill",
                "P45_OR_P60_FORM, P45 or P60 form",
                "STATEMENT_OF_OR_ENTITLEMENT_TO_BENEFITS, Statement of or entitlement to benefits",
                "PASSPORT, Passport",
                "EEA_ISSUED_ID_CARD, EEA issued ID card",
                "PHOTOCARD_DRIVING_LICENCE, Photo card driving licence",
                "BIOMETRIC_IDENTITY_DOCUMENT, Biometric identity document",
                "NI_ELECTORAL_IDENTITY_CARD, NI electoral identity card",
                "CD_ISSUED_DRIVING_LICENCE, Driving licence granted by a Crown Dependency",
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
                "BIRTH_CERTIFICATE, Tystysgrif geni",
                "MARRIAGE_OR_CIVIL_PARTNERSHIP_CERTIFICATE, Tystysgrif priodas neu bartneriaeth sifil",
                "ADOPTION_CERTIFICATE, Tystysgrif mabwysiadu",
                "FIREARMS_CERTIFICATE, Tystysgrif dryll",
                "UK_CD_ISSUED_NON_PHOTO_CARD_DRIVING_LICENCE, Fersiwn papur hen arddull o drwydded yrru gyfredol",
                "NON_UK_CD_ISSUED_DRIVING_LICENCE, Trwydded yrru ffotograffig gyfredol",
                "POLICE_BAIL_SHEET, Taflen mechnïaeth yr heddlu",
                "MORTGAGE_STATEMENT, Datganiad morgais",
                "BANK_OR_BUILDING_SOCIETY_STATEMENT_OR_CONFIRMATION_LETTER, Datganiad banc neu gymdeithas adeiladu neu lythyr cadarnhad",
                "CREDIT_CARD_STATEMENT, Datganiad cerdyn credyd",
                "PENSION_STATEMENT, Datganiad pensiwn",
                "COUNCIL_TAX_STATEMENT_OR_DEMAND_LETTER, Datganiad treth gyngor neu lythyr hawlio",
                "UTILITY_BILL, Bil cyfleustodau",
                "P45_OR_P60_FORM, Ffurflen P45 neu P60",
                "STATEMENT_OF_OR_ENTITLEMENT_TO_BENEFITS, Datganiad neu hawl i fudd-daliadau",
                "PASSPORT, Pasbort",
                "EEA_ISSUED_ID_CARD, Cerdyn adnabod a gyhoeddwyd gan yr AEE",
                "PHOTOCARD_DRIVING_LICENCE, Trwydded yrru cerdyn llun",
                "BIOMETRIC_IDENTITY_DOCUMENT, Dogfen hunaniaeth fiometrig",
                "NI_ELECTORAL_IDENTITY_CARD, Cerdyn adnabod etholiadol YG",
                "CD_ISSUED_DRIVING_LICENCE, Trwydded yrru a roddwyd gan Dibyniaeth y Goron",
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
                "BIRTH_CERTIFICATE, Birth certificate",
                "MARRIAGE_OR_CIVIL_PARTNERSHIP_CERTIFICATE, Marriage or civil partnership certificate",
                "ADOPTION_CERTIFICATE, Adoption certificate",
                "FIREARMS_CERTIFICATE, Firearms certificate",
                "UK_CD_ISSUED_NON_PHOTO_CARD_DRIVING_LICENCE, Old style paper version of a current driving licence",
                "NON_UK_CD_ISSUED_DRIVING_LICENCE, Current photo driving licence",
                "POLICE_BAIL_SHEET, Police bail sheet",
                "MORTGAGE_STATEMENT, Mortgage statement",
                "BANK_OR_BUILDING_SOCIETY_STATEMENT_OR_CONFIRMATION_LETTER, Bank or building society statement or confirmation letter",
                "CREDIT_CARD_STATEMENT, Credit card statement",
                "PENSION_STATEMENT, Pension statement",
                "COUNCIL_TAX_STATEMENT_OR_DEMAND_LETTER, Council tax statement or demand letter",
                "UTILITY_BILL, Utility bill",
                "P45_OR_P60_FORM, P45 or P60 form",
                "STATEMENT_OF_OR_ENTITLEMENT_TO_BENEFITS, Statement of or entitlement to benefits",
                "PASSPORT, Passport",
                "EEA_ISSUED_ID_CARD, EEA issued ID card",
                "PHOTOCARD_DRIVING_LICENCE, Photo card driving licence",
                "BIOMETRIC_IDENTITY_DOCUMENT, Biometric identity document",
                "NI_ELECTORAL_IDENTITY_CARD, NI electoral identity card",
                "CD_ISSUED_DRIVING_LICENCE, Driving licence granted by a Crown Dependency",
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
                "BIRTH_CERTIFICATE, Tystysgrif geni",
                "MARRIAGE_OR_CIVIL_PARTNERSHIP_CERTIFICATE, Tystysgrif priodas neu bartneriaeth sifil",
                "ADOPTION_CERTIFICATE, Tystysgrif mabwysiadu",
                "FIREARMS_CERTIFICATE, Tystysgrif dryll",
                "UK_CD_ISSUED_NON_PHOTO_CARD_DRIVING_LICENCE, Fersiwn papur hen arddull o drwydded yrru gyfredol",
                "NON_UK_CD_ISSUED_DRIVING_LICENCE, Trwydded yrru ffotograffig gyfredol",
                "POLICE_BAIL_SHEET, Taflen mechnïaeth yr heddlu",
                "MORTGAGE_STATEMENT, Datganiad morgais",
                "BANK_OR_BUILDING_SOCIETY_STATEMENT_OR_CONFIRMATION_LETTER, Datganiad banc neu gymdeithas adeiladu neu lythyr cadarnhad",
                "CREDIT_CARD_STATEMENT, Datganiad cerdyn credyd",
                "PENSION_STATEMENT, Datganiad pensiwn",
                "COUNCIL_TAX_STATEMENT_OR_DEMAND_LETTER, Datganiad treth gyngor neu lythyr hawlio",
                "UTILITY_BILL, Bil cyfleustodau",
                "P45_OR_P60_FORM, Ffurflen P45 neu P60",
                "STATEMENT_OF_OR_ENTITLEMENT_TO_BENEFITS, Datganiad neu hawl i fudd-daliadau",
                "PASSPORT, Pasbort",
                "EEA_ISSUED_ID_CARD, Cerdyn adnabod a gyhoeddwyd gan yr AEE",
                "PHOTOCARD_DRIVING_LICENCE, Trwydded yrru cerdyn llun",
                "BIOMETRIC_IDENTITY_DOCUMENT, Dogfen hunaniaeth fiometrig",
                "NI_ELECTORAL_IDENTITY_CARD, Cerdyn adnabod etholiadol YG",
                "CD_ISSUED_DRIVING_LICENCE, Trwydded yrru a roddwyd gan Dibyniaeth y Goron",
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
