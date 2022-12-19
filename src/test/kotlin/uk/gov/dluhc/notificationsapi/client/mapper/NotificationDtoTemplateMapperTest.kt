package uk.gov.dluhc.notificationsapi.client.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.dluhc.notificationsapi.config.NotifyTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.models.TemplateType

@ExtendWith(MockitoExtension::class)
internal class NotificationDtoTemplateMapperTest {

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    private lateinit var mapper: NotificationTemplateMapper

    @BeforeEach
    fun setupService() {
        mapper = NotificationTemplateMapper(
            NotifyTemplateConfiguration(
                receivedEmail = "RECEIVED-ID",
                approvedEmail = "APPROVED-ID",
                rejectedEmail = "REJECTED-ID",
                photoResubmissionEmail = "RESUBMISSION-ID"
            ),
            notificationTypeMapper
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_RECEIVED, RECEIVED-ID",
            "APPLICATION_REJECTED, REJECTED-ID",
            "APPLICATION_APPROVED, APPROVED-ID",
            "PHOTO_RESUBMISSION, RESUBMISSION-ID"
        ]
    )
    fun `should map Notification Type to Notify Template ID`(notificationType: NotificationType, expected: String) {
        // Given
        // When
        val notifyTemplateId = mapper.fromNotificationType(notificationType)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verifyNoInteractions(notificationTypeMapper)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED, REJECTED-ID",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED, APPROVED-ID",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION, RESUBMISSION-ID"
        ]
    )
    fun `should map Template Type to Notify Template ID`(
        templateType: TemplateType,
        mockedNotificationType: NotificationType,
        expected: String
    ) {
        // Given
        given(notificationTypeMapper.toNotificationType(any())).willReturn(mockedNotificationType)
        // When
        val notifyTemplateId = mapper.fromTemplateType(templateType)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verify(notificationTypeMapper).toNotificationType(templateType)
    }
}
