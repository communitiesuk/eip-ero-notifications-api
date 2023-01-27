package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason

class ApplicationRejectionReasonMapperTest {
    private val mapper = ApplicationRejectionReasonMapperImpl()
    @ParameterizedTest
    @CsvSource(
        value = [
            "INCOMPLETE_MINUS_APPLICATION, Application is incomplete",
            "INACCURATE_MINUS_INFORMATION, Application contains inaccurate information",
            "PHOTO_MINUS_IS_MINUS_NOT_MINUS_ACCEPTABLE, Photo does not meet criteria",
            "NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT, Applicant has not responded to requests for information",
            "FRAUDULENT_MINUS_APPLICATION, Suspected fraudulent application",
            "NOT_MINUS_REGISTERED_MINUS_TO_MINUS_VOTE, Applicant is not registered to vote",
            "OTHER, Other"
        ]
    )
    fun `should map enums to human readable messages`(rejectionReason: ApplicationRejectionReason, expected: String) {
        // Given
        // When
        val actual = mapper.toApplicationRejectionReasonMessage(rejectionReason)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
