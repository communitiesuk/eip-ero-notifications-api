package uk.gov.dluhc.notificationsapi.client.mapper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.OverseasNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.PostalNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.PostalNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.ProxyNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.ProxyNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.VoterCardNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.VoterCardNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.LETTER
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.NotificationTemplateNotFoundException

internal class NotificationTemplateMapperTest {

    private val mapper = NotificationTemplateMapper(
        NotifyEmailTemplateConfiguration(
            voterCard = VoterCardNotifyEmailTemplateConfiguration(
                receivedEnglish = "RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "RECEIVED-ID-EMAIL-WELSH",
                approvedEnglish = "APPROVED-ID-EMAIL-ENGLISH",
                approvedWelsh = "APPROVED-ID-EMAIL-WELSH",
                photoResubmissionEnglish = "PHOTO-RESUBMISSION-EMAIL-ENGLISH",
                photoResubmissionWelsh = "PHOTO-RESUBMISSION-EMAIL-WELSH",
                photoResubmissionWithReasonsEnglish = "PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
                photoResubmissionWithReasonsWelsh = "PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
                idDocumentResubmissionEnglish = "ID-DOCUMENT-RESUBMISSION-EMAIL-ENGLISH",
                idDocumentResubmissionWelsh = "ID-DOCUMENT-RESUBMISSION-EMAIL-WELSH",
                idDocumentResubmissionWithReasonsEnglish = "ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
                idDocumentResubmissionWithReasonsWelsh = "ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
                idDocumentRequiredEnglish = "ID-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
                idDocumentRequiredWelsh = "ID-DOCUMENT-REQUIRED-EMAIL-WELSH"
            ),
            postal = PostalNotifyEmailTemplateConfiguration(
                receivedEnglish = "POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "POSTAL-RECEIVED-ID-EMAIL-WELSH",
                rejectedDocumentEnglish = "POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
                rejectedDocumentWelsh = "POSTAL-REJECTED-DOCUMENT-EMAIL-WELSH"
            ),
            proxy = ProxyNotifyEmailTemplateConfiguration(
                receivedEnglish = "PROXY-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "PROXY-RECEIVED-ID-EMAIL-WELSH",
                rejectedDocumentEnglish = "PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
                rejectedDocumentWelsh = "PROXY-REJECTED-DOCUMENT-EMAIL-WELSH",
                rejectedSignatureEnglish = "PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",
                rejectedSignatureWelsh = "PROXY-REJECTED-SIGNATURE-EMAIL-WELSH",
            ),
            overseas = OverseasNotifyEmailTemplateConfiguration(
                receivedEnglish = "OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "OVERSEAS-RECEIVED-ID-EMAIL-WELSH",
            ),
        ),
        NotifyLetterTemplateConfiguration(
            voterCard = VoterCardNotifyLetterTemplateConfiguration(
                rejectedEnglish = "REJECTED-ID-LETTER-ENGLISH",
                rejectedWelsh = "REJECTED-ID-LETTER-WELSH",
                photoResubmissionEnglish = "PHOTO-RESUBMISSION-LETTER-ENGLISH",
                photoResubmissionWelsh = "PHOTO-RESUBMISSION-LETTER-WELSH",
                photoResubmissionWithReasonsEnglish = "PHOTO-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
                photoResubmissionWithReasonsWelsh = "PHOTO-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
                idDocumentResubmissionEnglish = "ID-DOCUMENT-RESUBMISSION-LETTER-ENGLISH",
                idDocumentResubmissionWelsh = "ID-DOCUMENT-RESUBMISSION-LETTER-WELSH",
                idDocumentResubmissionWithReasonsEnglish = "ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
                idDocumentResubmissionWithReasonsWelsh = "ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
                idDocumentRequiredEnglish = "ID-DOCUMENT-REQUIRED-LETTER-ENGLISH",
                idDocumentRequiredWelsh = "ID-DOCUMENT-REQUIRED-LETTER-WELSH"
            ),
            postal = PostalNotifyLetterTemplateConfiguration(
                rejectedDocumentEnglish = "POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
                rejectedDocumentWelsh = "POSTAL-REJECTED-DOCUMENT-LETTER-WELSH"
            ),
            proxy = ProxyNotifyLetterTemplateConfiguration(
                rejectedDocumentEnglish = "PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
                rejectedDocumentWelsh = "PROXY-REJECTED-DOCUMENT-LETTER-WELSH",
                rejectedSignatureEnglish = "PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",
                rejectedSignatureWelsh = "PROXY-REJECTED-SIGNATURE-LETTER-WELSH",
            )
        )
    )

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "VOTER_CARD,,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-LETTER-ENGLISH",
            "POSTAL,,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-LETTER-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,ENGLISH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-LETTER-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-LETTER-WELSH"
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
            "VOTER_CARD,,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
            "POSTAL,,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            "POSTAL,,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-ENGLISH",
            "OVERSEAS,,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,ENGLISH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-EMAIL-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-EMAIL-WELSH",
            "POSTAL,WELSH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-WELSH",
            "PROXY,WELSH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-WELSH",
            "OVERSEAS,WELSH,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-WELSH"
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
            "PROXY,ENGLISH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",
            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "POSTAL,WELSH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-WELSH",
            "PROXY,WELSH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-WELSH",
            "OVERSEAS,WELSH,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-WELSH"
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
            "POSTAL,WELSH,REJECTED_SIGNATURE",
            "POSTAL,WELSH,REJECTED_DOCUMENT",
            "PROXY,WELSH,REJECTED_DOCUMENT"
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
            .isInstanceOfAny(NotificationTemplateNotFoundException::class.java)
            .hasMessage("No letter template defined in ${language.toMessage()} for notification type $notificationType and sourceType $sourceType")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,LETTER,APPLICATION_APPROVED, letter",
            "VOTER_CARD,ENGLISH,LETTER,APPLICATION_APPROVED, letter",
            "VOTER_CARD,WELSH,LETTER,APPLICATION_APPROVED, letter",
            "POSTAL,WELSH,LETTER,REJECTED_SIGNATURE, letter",
            "POSTAL,WELSH,LETTER, REJECTED_DOCUMENT, letter",
            "PROXY,WELSH,LETTER, REJECTED_DOCUMENT, letter"
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
            .isInstanceOfAny(NotificationTemplateNotFoundException::class.java)
            .hasMessage("No $channelString template defined in ${language.toMessage()} for notification type $templateType and sourceType $sourceType")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,EMAIL,APPLICATION_REJECTED, email",
            "VOTER_CARD,ENGLISH,EMAIL,APPLICATION_REJECTED, email",
            "VOTER_CARD,WELSH,EMAIL,APPLICATION_REJECTED, email",
            "POSTAL,WELSH,EMAIL, REJECTED_SIGNATURE, email",
            "POSTAL,WELSH,EMAIL, REJECTED_DOCUMENT, email",
            "PROXY,WELSH,EMAIL, REJECTED_DOCUMENT, email"
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
            .isInstanceOfAny(NotificationTemplateNotFoundException::class.java)
            .hasMessage("No $channelString template defined in ${language.toMessage()} for notification type $templateType and sourceType $sourceType")
    }

    @ParameterizedTest
    @EnumSource(LanguageDto::class)
    fun `should fail to map to Application Received Template ID when the Source Type is Anonymous and Application Received`(
        language: LanguageDto,
    ) {
        // Given
        val sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT
        val notificationType: NotificationType = NotificationType.APPLICATION_RECEIVED

        // When
        val error = catchException { mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, EMAIL, language) }

        // Then
        assertThat(error)
            .isInstanceOfAny(NotificationTemplateNotFoundException::class.java)
            .hasMessage("No email template configuration defined for sourceType ANONYMOUS_ELECTOR_DOCUMENT")
    }
}

private fun LanguageDto?.toMessage(): String = if (this == LanguageDto.WELSH) "Welsh" else "English"
