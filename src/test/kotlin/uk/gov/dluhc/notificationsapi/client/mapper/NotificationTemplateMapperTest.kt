package uk.gov.dluhc.notificationsapi.client.mapper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.LETTER
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.InvalidSourceTypeException

internal class NotificationTemplateMapperTest {

    private val mapper = NotificationTemplateMapper(
        NotifyEmailTemplateConfiguration(
            receivedEnglish = "RECEIVED-ID-EMAIL-ENGLISH",
            receivedWelsh = "RECEIVED-ID-EMAIL-WELSH",
            postalReceivedEnglish = "POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            postalReceivedWelsh = "POSTAL-RECEIVED-ID-EMAIL-WELSH",
            approvedEnglish = "APPROVED-ID-EMAIL-ENGLISH",
            approvedWelsh = "APPROVED-ID-EMAIL-WELSH",
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
            "VOTER_CARD,,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "VOTER_CARD,,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "VOTER_CARD,,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-LETTER-WELSH",
            "VOTER_CARD,WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-LETTER-WELSH"
        ]
    )
    fun `should map Notification Type in language for letter channel to Notify Template ID`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, LETTER, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,,APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",
            "POSTAL,,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, DOCUMENT-RESUBMISSION-ID-EMAIL-WELSH",
            "POSTAL,WELSH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-WELSH"
        ]
    )
    fun `should map Notification Type in language for email channel to Notify Template ID`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // Given

        // When
        val notifyTemplateId = mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "POSTAL,WELSH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-WELSH"
        ]
    )
    fun `should map Source Type in language for email channel to Notify Template ID`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String
    ) {
        // When

        val notifyTemplateId = mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,APPLICATION_APPROVED",

            "VOTER_CARD,ENGLISH,APPLICATION_APPROVED",

            "VOTER_CARD,WELSH,APPLICATION_APPROVED",
        ]
    )
    fun `should fail to map Notification Type in language for letter channel for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
    ) {
        // Given

        // When
        val error =
            catchException { mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, LETTER, language) }

        // Then
        assertThat(error)
            .isInstanceOfAny(IllegalStateException::class.java)
            .hasMessage("No letter template defined in ${language.toMessage()} for notification type $notificationType")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,LETTER,APPLICATION_APPROVED, letter",
            "VOTER_CARD,ENGLISH,LETTER,APPLICATION_APPROVED, letter",
            "VOTER_CARD,WELSH,LETTER,APPLICATION_APPROVED, letter"
        ]
    )
    fun `should fail to map letter Template Type in language for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        channel: NotificationChannel,
        templateType: NotificationType,
        channelString: String
    ) {
        // Given

        // When
        val error = catchException { mapper.fromNotificationTypeForChannelInLanguage(sourceType, templateType, channel, language) }

        // Then
        assertThat(error)
            .isInstanceOfAny(IllegalStateException::class.java)
            .hasMessage("No $channelString template defined in ${language.toMessage()} for notification type $templateType")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,EMAIL,APPLICATION_REJECTED, email",
            "VOTER_CARD,ENGLISH,EMAIL,APPLICATION_REJECTED, email",
            "VOTER_CARD,WELSH,EMAIL,APPLICATION_REJECTED, email",
        ]
    )
    fun `should fail to map email Template Type in language for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        channel: NotificationChannel,
        templateType: NotificationType,
        channelString: String
    ) {
        // Given

        // When
        val error = catchException { mapper.fromNotificationTypeForChannelInLanguage(sourceType, templateType, channel, language) }

        // Then
        assertThat(error)
            .isInstanceOfAny(IllegalStateException::class.java)
            .hasMessage("No $channelString template defined in ${language.toMessage()} for notification type $templateType and sourceType $sourceType")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "ENGLISH, APPLICATION_RECEIVED",
            "WELSH, APPLICATION_RECEIVED",
        ]
    )
    fun `should fail to map to Application Received Template ID when the Source Type is Anonymous Elector Document`(
        language: LanguageDto,
        notificationType: NotificationType,
    ) {
        // Given
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT

        // When
        val error = catchException { mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, EMAIL, language) }

        // Then
        assertThat(error)
            .isInstanceOfAny(InvalidSourceTypeException::class.java)
            .hasMessage("No email template defined in ${language.toMessage()} for source type ANONYMOUS_ELECTOR_DOCUMENT")
    }
}

private fun LanguageDto?.toMessage(): String = if (this == LanguageDto.WELSH) "Welsh" else "English"
