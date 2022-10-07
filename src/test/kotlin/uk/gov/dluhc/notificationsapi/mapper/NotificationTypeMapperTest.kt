package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.models.TemplateType

class NotificationTypeMapperTest {
    private val mapper = NotificationTypeMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION",
        ]
    )
    fun `should map template type to notification type`(source: TemplateType, expected: NotificationType) {
        // Given

        // When
        val actual = mapper.toNotificationType(source)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
