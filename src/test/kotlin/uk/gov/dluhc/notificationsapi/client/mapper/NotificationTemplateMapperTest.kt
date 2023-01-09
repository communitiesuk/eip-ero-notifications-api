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
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.LETTER
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.TemplateType
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper

@ExtendWith(MockitoExtension::class)
internal class NotificationTemplateMapperTest {

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    private lateinit var mapper: NotificationTemplateMapper

    @BeforeEach
    fun setupService() {
        mapper = NotificationTemplateMapper(
            NotifyEmailTemplateConfiguration(
                receivedEnglish = "RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "RECEIVED-ID-EMAIL-WELSH",
                approvedEnglish = "APPROVED-ID-EMAIL-ENGLISH",
                approvedWelsh = "APPROVED-ID-EMAIL-WELSH",
                rejectedEnglish = "REJECTED-ID-EMAIL-ENGLISH",
                rejectedWelsh = "REJECTED-ID-EMAIL-WELSH",
                photoResubmissionEnglish = "PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
                photoResubmissionWelsh = "PHOTO-RESUBMISSION-ID-EMAIL-WELSH",
                idDocumentResubmissionEnglish = "DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",
                idDocumentResubmissionWelsh = "DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH"
            ),
            NotifyLetterTemplateConfiguration(
                receivedEnglish = "RECEIVED-ID-LETTER-ENGLISH",
                receivedWelsh = "RECEIVED-ID-LETTER-WELSH",
                approvedEnglish = "APPROVED-ID-LETTER-ENGLISH",
                approvedWelsh = "APPROVED-ID-LETTER-WELSH",
                rejectedEnglish = "REJECTED-ID-LETTER-ENGLISH",
                rejectedWelsh = "REJECTED-ID-LETTER-WELSH",
                photoResubmissionEnglish = "PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
                photoResubmissionWelsh = "PHOTO-RESUBMISSION-ID-LETTER-WELSH",
                idDocumentResubmissionEnglish = "DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",
                idDocumentResubmissionWelsh = "DOCUMENT-RESUBMISSION-ID-LETTER-WELSH"
            ),
            notificationTypeMapper
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            ",APPLICATION_REJECTED, REJECTED-ID-EMAIL-ENGLISH",
            ",APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, REJECTED-ID-EMAIL-ENGLISH",
            "ENGLISH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            "ENGLISH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",

            "WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_REJECTED, REJECTED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH"
        ]
    )
    fun `should map Notification Type in language for email channel to Notify Template ID`(
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromNotificationTypeForChannelInLanguage(notificationType, EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verifyNoInteractions(notificationTypeMapper)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            ",APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-EMAIL-ENGLISH",
            ",APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-EMAIL-ENGLISH",
            "ENGLISH,APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH",

            "WELSH,APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH",
        ]
    )
    fun `should map Template Type in language for email channel to Notify Template ID`(
        language: LanguageDto?,
        templateType: TemplateType,
        mockedNotificationType: NotificationType,
        expected: String
    ) {
        // Given
        given(notificationTypeMapper.toNotificationType(any())).willReturn(mockedNotificationType)

        // When
        val notifyTemplateId = mapper.fromTemplateTypeForChannelAndLanguage(templateType, EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verify(notificationTypeMapper).toNotificationType(templateType)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            ",APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            ",APPLICATION_APPROVED, APPROVED-ID-LETTER-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_APPROVED, APPROVED-ID-LETTER-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "ENGLISH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "WELSH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_APPROVED, APPROVED-ID-LETTER-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH"
        ]
    )
    fun `should map Notification Type in language for letter channel to Notify Template ID`(
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromNotificationTypeForChannelInLanguage(notificationType, LETTER, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verifyNoInteractions(notificationTypeMapper)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            ",APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            ",APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-LETTER-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-LETTER-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH",

            "WELSH,APPLICATION_RECEIVED, APPLICATION_RECEIVED, RECEIVED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_REJECTED, APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_APPROVED, APPLICATION_APPROVED, APPROVED-ID-LETTER-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH",
        ]
    )
    fun `should map Template Type in language for letter channel to Notify Template ID`(
        language: LanguageDto?,
        templateType: TemplateType,
        mockedNotificationType: NotificationType,
        expected: String
    ) {
        // Given
        given(notificationTypeMapper.toNotificationType(any())).willReturn(mockedNotificationType)

        // When
        val notifyTemplateId = mapper.fromTemplateTypeForChannelAndLanguage(templateType, LETTER, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
        verify(notificationTypeMapper).toNotificationType(templateType)
    }
}
