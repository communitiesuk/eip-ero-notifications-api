package uk.gov.dluhc.notificationsapi.client.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.LETTER
import uk.gov.dluhc.notificationsapi.dto.NotificationType

internal class NotificationTemplateMapperTest {

    private val mapper = NotificationTemplateMapper(
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
            rejectedEnglish = "REJECTED-ID-LETTER-ENGLISH",
            rejectedWelsh = "REJECTED-ID-LETTER-WELSH",
            photoResubmissionEnglish = "PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            photoResubmissionWelsh = "PHOTO-RESUBMISSION-ID-LETTER-WELSH",
            idDocumentResubmissionEnglish = "DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",
            idDocumentResubmissionWelsh = "DOCUMENT-RESUBMISSION-ID-LETTER-WELSH"
        )
    )

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
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH",

            "WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_REJECTED, REJECTED-ID-EMAIL-WELSH",
            "WELSH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH",
        ]
    )
    fun `should map Template Type in language for email channel to Notify Template ID`(
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromTemplateTypeForChannelAndLanguage(notificationType, EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            ",APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "ENGLISH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "WELSH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
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
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ",APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            ",APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            ",PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            ",ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH",

            "WELSH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-WELSH",
            "WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-WELSH",
            "WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH",
        ]
    )
    fun `should map Template Type in language for letter channel to Notify Template ID`(
        language: LanguageDto?,
        templateType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromTemplateTypeForChannelAndLanguage(templateType, LETTER, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }
}
