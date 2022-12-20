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
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.models.TemplateType

@ExtendWith(MockitoExtension::class)
internal class NotificationTemplateMapperTest {

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    private lateinit var mapper: NotificationTemplateMapper

    @BeforeEach
    fun setupService() {
        mapper = NotificationTemplateMapper(
            NotifyTemplateConfiguration(
                receivedEmailEnglish = "RECEIVED-ID-ENGLISH",
                receivedEmailWelsh = "RECEIVED-ID-WELSH",
                approvedEmailEnglish = "APPROVED-ID-ENGLISH",
                approvedEmailWelsh = "APPROVED-ID-WELSH",
                rejectedEmailEnglish = "REJECTED-ID-ENGLISH",
                rejectedEmailWelsh = "REJECTED-ID-WELSH",
                photoResubmissionEmailEnglish = "RESUBMISSION-ID-ENGLISH",
                photoResubmissionEmailWelsh = "RESUBMISSION-ID-WELSH"
            ),
            notificationTypeMapper
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ", APPLICATION_RECEIVED, RECEIVED-ID-ENGLISH",
            ", APPLICATION_REJECTED, REJECTED-ID-ENGLISH",
            ", APPLICATION_APPROVED, APPROVED-ID-ENGLISH",
            ", PHOTO_RESUBMISSION, RESUBMISSION-ID-ENGLISH",

            "EN, APPLICATION_RECEIVED, RECEIVED-ID-ENGLISH",
            "EN, APPLICATION_REJECTED, REJECTED-ID-ENGLISH",
            "EN, APPLICATION_APPROVED, APPROVED-ID-ENGLISH",
            "EN, PHOTO_RESUBMISSION, RESUBMISSION-ID-ENGLISH",

            "CY, APPLICATION_RECEIVED, RECEIVED-ID-WELSH",
            "CY, APPLICATION_REJECTED, REJECTED-ID-WELSH",
            "CY, APPLICATION_APPROVED, APPROVED-ID-WELSH",
            "CY, PHOTO_RESUBMISSION, RESUBMISSION-ID-WELSH"
        ]
    )
    fun `should map Notification Type in language for channel to Notify Template ID`(
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given
        // When
        val notifyTemplateId = mapper.fromNotificationTypeInLanguageForChannel(notificationType, language, EMAIL)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verifyNoInteractions(notificationTypeMapper)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-ENGLISH",
            ",APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED, REJECTED-ID-ENGLISH",
            ",APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED, APPROVED-ID-ENGLISH",
            ",PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION, RESUBMISSION-ID-ENGLISH",

            "EN,APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-ENGLISH",
            "EN,APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED, REJECTED-ID-ENGLISH",
            "EN,APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED, APPROVED-ID-ENGLISH",
            "EN,PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION, RESUBMISSION-ID-ENGLISH",

            "CY,APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-WELSH",
            "CY,APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED, REJECTED-ID-WELSH",
            "CY,APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED, APPROVED-ID-WELSH",
            "CY,PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION, RESUBMISSION-ID-WELSH",
        ]
    )
    fun `should map Template Type in language for channel to Notify Template ID`(
        language: LanguageDto?,
        templateType: TemplateType,
        mockedNotificationType: NotificationType,
        expected: String
    ) {
        // Given
        given(notificationTypeMapper.toNotificationType(any())).willReturn(mockedNotificationType)
        // When
        val notifyTemplateId = mapper.fromTemplateTypeForChannelAndLanguage(templateType, language, EMAIL)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verify(notificationTypeMapper).toNotificationType(templateType)
    }
}
