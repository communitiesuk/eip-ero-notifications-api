package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.context.MessageSource
import java.util.Locale.ENGLISH
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason as ApplicationRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason as ApplicationRejectionReasonApiEnum

@ExtendWith(MockitoExtension::class)
class ApplicationRejectionReasonMapperTest {
    @InjectMocks
    private lateinit var mapper: ApplicationRejectionReasonMapper

    @Mock
    private lateinit var messageSource: MessageSource

    @Nested
    inner class ToApplicationRejectionReasonStringFromApiEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, incomplete-application, Application is incomplete",
                "INACCURATE_MINUS_INFORMATION, inaccurate-information, Application contains inaccurate information",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, photo-is-not-acceptable, Photo does not meet criteria",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, no-response-from-applicant, Applicant has not responded to requests for information",
                "FRAUDULENT_MINUS_APPLICATION, fraudulent-application, Suspected fraudulent application",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, not-registered-to-vote, Applicant is not registered to vote",
                "OTHER, other, Other"
            ]
        )
        fun `should map enums to human readable messages`(
            rejectionReason: ApplicationRejectionReasonApiEnum,
            keySuffix: String,
            value: String
        ) {
            // Given
            val key = "templates.application-rejection.rejection-reasons.$keySuffix"
            given(messageSource.getMessage(any(), anyOrNull(), any())).willReturn(value)

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason)

            // Then
            assertThat(actual).isEqualTo(value)
            verify(messageSource).getMessage(key, null, ENGLISH)
        }
    }

    @Nested
    inner class ToApplicationRejectionReasonStringFromMessageEnum {
        @ParameterizedTest
        @CsvSource(
            value = [
                "INCOMPLETE_MINUS_APPLICATION, incomplete-application, Application is incomplete",
                "INACCURATE_MINUS_INFORMATION, inaccurate-information, Application contains inaccurate information",
                "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, photo-is-not-acceptable, Photo does not meet criteria",
                "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, no-response-from-applicant, Applicant has not responded to requests for information",
                "FRAUDULENT_MINUS_APPLICATION, fraudulent-application, Suspected fraudulent application",
                "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, not-registered-to-vote, Applicant is not registered to vote",
                "OTHER, other, Other"
            ]
        )
        fun `should map enums to human readable messages`(
            rejectionReason: ApplicationRejectionReasonMessageEnum,
            keySuffix: String,
            value: String
        ) {
            // Given
            val key = "templates.application-rejection.rejection-reasons.$keySuffix"
            given(messageSource.getMessage(any(), anyOrNull(), any())).willReturn(value)

            // When
            val actual = mapper.toApplicationRejectionReasonString(rejectionReason)

            // Then
            assertThat(actual).isEqualTo(value)
            verify(messageSource).getMessage(key, null, ENGLISH)
        }
    }
}
