package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity

class NotificationTypeMapperTest {
    private val mapper = NotificationTypeMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION",
            "ID_MINUS_DOCUMENT_MINUS_RESUBMISSION, ID_DOCUMENT_RESUBMISSION",
        ]
    )
    fun `should map Message Type to NotificationType `(messageType: MessageType, expected: NotificationType) {
        // Given

        // When
        val actual = mapper.mapMessageTypeToNotificationType(messageType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_REJECTED, APPLICATION_REJECTED",
            "APPLICATION_APPROVED, APPLICATION_APPROVED",
            "PHOTO_RESUBMISSION, PHOTO_RESUBMISSION",
            "ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION"
        ]
    )
    fun `should map DTO Notification Type to Entity Notification Type`(
        dtoType: NotificationType,
        expected: NotificationTypeEntity
    ) {
        // Given

        // When
        val actual = mapper.toNotificationTypeEntity(dtoType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
