package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType as NotificationTypeEntity

class NotificationTypeMapperTest {
    private val mapper = NotificationTypeMapper()

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION",
            "ID_MINUS_DOCUMENT_MINUS_RESUBMISSION, ID_DOCUMENT_RESUBMISSION",
            "ID_MINUS_DOCUMENT_MINUS_REQUIRED, ID_DOCUMENT_REQUIRED",
            "REJECTED_MINUS_DOCUMENT, REJECTED_DOCUMENT",
            "REJECTED_MINUS_SIGNATURE, REJECTED_SIGNATURE",
            "REQUESTED_MINUS_SIGNATURE, REQUESTED_SIGNATURE",
            "NINO_MINUS_NOT_MINUS_MATCHED, NINO_NOT_MATCHED",
            "SIGNATURE_MINUS_RESUBMISSION, SIGNATURE_RESUBMISSION",
        ],
    )
    fun `should map Message Type to NotificationType`(messageType: MessageType, expected: NotificationType) {
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
            "PHOTO_RESUBMISSION_WITH_REASONS, PHOTO_RESUBMISSION",
            "ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION",
            "ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID_DOCUMENT_RESUBMISSION",
            "ID_DOCUMENT_REQUIRED, ID_DOCUMENT_REQUIRED",
            "REJECTED_DOCUMENT, REJECTED_DOCUMENT",
            "REJECTED_SIGNATURE, REJECTED_SIGNATURE",
            "REJECTED_SIGNATURE_WITH_REASONS, REJECTED_SIGNATURE",
            "REQUESTED_SIGNATURE, REQUESTED_SIGNATURE",
            "NINO_NOT_MATCHED, NINO_NOT_MATCHED",
            "NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, NINO_NOT_MATCHED",
            "SIGNATURE_RESUBMISSION, SIGNATURE_RESUBMISSION",
            "SIGNATURE_RESUBMISSION_WITH_REASONS, SIGNATURE_RESUBMISSION",
        ],
    )
    fun `should map DTO Notification Type to Entity Notification Type`(
        dtoType: NotificationType,
        expected: NotificationTypeEntity,
    ) {
        // Given

        // When
        val actual = mapper.toNotificationTypeEntity(dtoType)

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
            "ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION",
            "ID_DOCUMENT_REQUIRED, ID_DOCUMENT_REQUIRED",
            "REJECTED_DOCUMENT, REJECTED_DOCUMENT",
            "REJECTED_SIGNATURE, REJECTED_SIGNATURE",
            "REQUESTED_SIGNATURE, REQUESTED_SIGNATURE",
            "NINO_NOT_MATCHED, NINO_NOT_MATCHED",
            "SIGNATURE_RESUBMISSION, SIGNATURE_RESUBMISSION",
        ],
    )
    fun `should map Entity Notification Type to Dto Notification Type`(
        entityType: NotificationTypeEntity,
        expected: NotificationType,
    ) {
        // Given

        // When
        val actual = mapper.toNotificationTypeDto(entityType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_RECEIVED, APPLICATION_MINUS_RECEIVED",
            "APPLICATION_APPROVED, APPLICATION_MINUS_APPROVED",
            "APPLICATION_REJECTED, APPLICATION_MINUS_REJECTED",
            "PHOTO_RESUBMISSION, PHOTO_MINUS_RESUBMISSION",
            "ID_DOCUMENT_RESUBMISSION, ID_MINUS_DOCUMENT_MINUS_RESUBMISSION",
            "ID_DOCUMENT_REQUIRED, ID_MINUS_DOCUMENT_MINUS_REQUIRED",
            "REJECTED_DOCUMENT, REJECTED_MINUS_DOCUMENT",
            "NINO_NOT_MATCHED, NINO_MINUS_NOT_MINUS_MATCHED",
            "REJECTED_SIGNATURE, REJECTED_MINUS_SIGNATURE",
            "REQUESTED_SIGNATURE, REQUESTED_MINUS_SIGNATURE",
            "NINO_NOT_MATCHED, NINO_MINUS_NOT_MINUS_MATCHED",
            "REJECTED_PARENT_GUARDIAN, REJECTED_MINUS_PARENT_MINUS_GUARDIAN",
            "REJECTED_PREVIOUS_ADDRESS, REJECTED_MINUS_PREVIOUS_MINUS_ADDRESS",
            "SIGNATURE_RESUBMISSION, SIGNATURE_MINUS_RESUBMISSION",
            "SIGNATURE_RECEIVED, SIGNATURE_MINUS_RECEIVED",
        ],
    )
    fun `should map Notification Type to Template Type`(notificationType: NotificationType, expected: TemplateType) {
        // Given

        // When
        val actual = mapper.fromNotificationTypeDtoToTemplateTypeApi(notificationType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @EnumSource(
        value = NotificationType::class,
        names = [
            "PHOTO_RESUBMISSION_WITH_REASONS",
            "ID_DOCUMENT_RESUBMISSION_WITH_REASONS",
            "REJECTED_SIGNATURE_WITH_REASONS",
            "NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
            "SIGNATURE_RESUBMISSION_WITH_REASONS",
        ],
    )
    fun `should not map Notification Type to Template Type given unsupported value`(unSupportedNotificationType: NotificationType) {
        // Given

        // When
        val exception = catchThrowableOfType(IllegalArgumentException::class.java) {
            mapper.fromNotificationTypeDtoToTemplateTypeApi(unSupportedNotificationType)
        }

        // Then
        assertThat(exception)
            .hasMessage("Unexpected NotificationType $unSupportedNotificationType when mapping to API TemplateType")
    }
}
