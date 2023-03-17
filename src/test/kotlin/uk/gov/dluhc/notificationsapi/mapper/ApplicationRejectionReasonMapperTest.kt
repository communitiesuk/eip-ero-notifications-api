package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason as ApplicationRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason as ApplicationRejectionReasonApiEnum

class ApplicationRejectionReasonMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = ApplicationRejectionReasonMapper(messageSource)

    @Nested
    inner class ToApplicationRejectionReasonStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, Your application was incomplete",
                "INACCURATE_MINUS_INFORMATION, Your application contained inaccurate information",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, Your photo did not meet the criteria",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, You did not respond to our requests for information within the timeframe we gave you",
                "FRAUDULENT_MINUS_APPLICATION, Your application contained inaccurate information",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, We have not been able to confirm that you are registered to vote at the address you provided",
                "DUPLICATE_MINUS_APPLICATION, We have received one or more duplicate or very similar applications for the details provided",
                "NO_MINUS_FRANCHISE_MINUS_TO_MINUS_VOTE, Our records show you are not eligible to vote in polls in Great Britain that require photo identification",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: ApplicationRejectionReasonApiEnum,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, Mae'r cais yn anghyflawn",
                "INACCURATE_MINUS_INFORMATION, Mae'r cais yn cynnwys gwybodaeth anghywir",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, Nid yw'r ffotograff yn bodloni'r meini prawf",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, Nid yw'r ymgeisydd wedi ymateb i geisiadau am wybodaeth",
                "FRAUDULENT_MINUS_APPLICATION, Mae'r cais yn cynnwys gwybodaeth anghywir",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, Nid yw'r ymgeisydd wedi'i gofrestru i bleidleisio",
                "DUPLICATE_MINUS_APPLICATION, Rydym wedi derbyn un neu fwy o geisiadau dyblyg neu debyg iawn am y manylion a ddarparwyd",
                "NO_MINUS_FRANCHISE_MINUS_TO_MINUS_VOTE, Mae ein cofnodion yn dangos nad ydych yn gymwys i bleidleisio mewn polau ym Mhrydain Fawr sydd angen prawf adnabod â llun",
                "OTHER, Eraill"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: ApplicationRejectionReasonApiEnum,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class ToApplicationRejectionReasonStringFromMessageEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, Your application was incomplete",
                "INACCURATE_MINUS_INFORMATION, Your application contained inaccurate information",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, Your photo did not meet the criteria",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, You did not respond to our requests for information within the timeframe we gave you",
                "FRAUDULENT_MINUS_APPLICATION, Your application contained inaccurate information",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, We have not been able to confirm that you are registered to vote at the address you provided",
                "DUPLICATE_MINUS_APPLICATION, We have received one or more duplicate or very similar applications for the details provided",
                "NO_MINUS_FRANCHISE_MINUS_TO_MINUS_VOTE, Our records show you are not eligible to vote in polls in Great Britain that require photo identification",
                "OTHER, Other"
            ]
        )
        fun `should map enums to human readable messages in English`(
            rejectionReason: ApplicationRejectionReasonMessageEnum,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason, LanguageDto.ENGLISH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, Mae'r cais yn anghyflawn",
                "INACCURATE_MINUS_INFORMATION, Mae'r cais yn cynnwys gwybodaeth anghywir",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, Nid yw'r ffotograff yn bodloni'r meini prawf",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, Nid yw'r ymgeisydd wedi ymateb i geisiadau am wybodaeth",
                "FRAUDULENT_MINUS_APPLICATION, Mae'r cais yn cynnwys gwybodaeth anghywir",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, Nid yw'r ymgeisydd wedi'i gofrestru i bleidleisio",
                "DUPLICATE_MINUS_APPLICATION, Rydym wedi derbyn un neu fwy o geisiadau dyblyg neu debyg iawn am y manylion a ddarparwyd",
                "NO_MINUS_FRANCHISE_MINUS_TO_MINUS_VOTE, Mae ein cofnodion yn dangos nad ydych yn gymwys i bleidleisio mewn polau ym Mhrydain Fawr sydd angen prawf adnabod â llun",
                "OTHER, Eraill"
            ]
        )
        fun `should map enums to human readable messages in Welsh`(
            rejectionReason: ApplicationRejectionReasonMessageEnum,
            expected: String
        ) {
            // Given

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason, LanguageDto.WELSH)

            // Then
            assertThat(actual).isEqualTo(expected)
        }
    }
}
