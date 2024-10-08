package uk.gov.dluhc.notificationsapi.client.mapper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.config.NotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.NotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.OverseasNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.OverseasNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.PostalNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.PostalNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.ProxyNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.ProxyNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.VoterCardNotifyEmailTemplateConfiguration
import uk.gov.dluhc.notificationsapi.config.VoterCardNotifyLetterTemplateConfiguration
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
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
                idDocumentRequiredWelsh = "ID-DOCUMENT-REQUIRED-EMAIL-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-EMAIL-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-EMAIL-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            ),
            postal = PostalNotifyEmailTemplateConfiguration(
                receivedEnglish = "POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "POSTAL-RECEIVED-ID-EMAIL-WELSH",
                rejectedDocumentEnglish = "POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
                rejectedDocumentWelsh = "POSTAL-REJECTED-DOCUMENT-EMAIL-WELSH",
                ninoNotMatchedEnglish = "POSTAL-NINO-NOT-MATCHED-EMAIL-ENGLISH",
                ninoNotMatchedWelsh = "POSTAL-NINO-NOT-MATCHED-EMAIL-WELSH",
                ninoNotMatchedRestrictedDocumentsListEnglish = "POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
                ninoNotMatchedRestrictedDocumentsListWelsh = "POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-WELSH",
                rejectedSignatureEnglish = "POSTAL-REJECTED-SIGNATURE-EMAIL-ENGLISH",
                rejectedSignatureWelsh = "POSTAL-REJECTED-SIGNATURE-EMAIL-WELSH",
                rejectedSignatureWithReasonsEnglish = "POSTAL-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
                rejectedSignatureWithReasonsWelsh = "POSTAL-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-WELSH",
                requestedSignatureEnglish = "POSTAL-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
                requestedSignatureWelsh = "POSTAL-REQUESTED-SIGNATURE-EMAIL-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-EMAIL-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-EMAIL-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            ),
            proxy = ProxyNotifyEmailTemplateConfiguration(
                receivedEnglish = "PROXY-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "PROXY-RECEIVED-ID-EMAIL-WELSH",
                rejectedDocumentEnglish = "PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
                rejectedDocumentWelsh = "PROXY-REJECTED-DOCUMENT-EMAIL-WELSH",
                rejectedSignatureEnglish = "PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",
                rejectedSignatureWelsh = "PROXY-REJECTED-SIGNATURE-EMAIL-WELSH",
                rejectedSignatureWithReasonsEnglish = "PROXY-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
                rejectedSignatureWithReasonsWelsh = "PROXY-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-WELSH",
                ninoNotMatchedWelsh = "PROXY-NINO-NOT-MATCHED-EMAIL-WELSH",
                ninoNotMatchedEnglish = "PROXY-NINO-NOT-MATCHED-EMAIL-ENGLISH",
                ninoNotMatchedRestrictedDocumentsListEnglish = "PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
                ninoNotMatchedRestrictedDocumentsListWelsh = "PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-WELSH",
                requestedSignatureEnglish = "PROXY-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
                requestedSignatureWelsh = "PROXY-REQUESTED-SIGNATURE-EMAIL-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-EMAIL-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-EMAIL-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            ),
            overseas = OverseasNotifyEmailTemplateConfiguration(
                receivedEnglish = "OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
                receivedWelsh = "OVERSEAS-RECEIVED-ID-EMAIL-WELSH",
                rejectedParentGuardianEnglish = "OVERSEAS-REJECTED-PARENT-GUARDIAN-EMAIL-ENGLISH",
                rejectedParentGuardianWelsh = "OVERSEAS-REJECTED-PARENT-GUARDIAN-EMAIL-WELSH",
                rejectedPreviousAddressEnglish = "OVERSEAS-REJECTED-PREVIOUS-ADDRESS-EMAIL-ENGLISH",
                rejectedPreviousAddressWelsh = "OVERSEAS-REJECTED-PREVIOUS-ADDRESS-EMAIL-WELSH",
                rejectedDocumentEnglish = "OVERSEAS-REJECTED-DOCUMENT-EMAIL-ENGLISH",
                rejectedDocumentWelsh = "OVERSEAS-REJECTED-DOCUMENT-EMAIL-WELSH",
                parentGuardianProofRequiredEnglish = "OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-EMAIL-ENGLISH",
                parentGuardianProofRequiredWelsh = "OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-EMAIL-WELSH",
                previousAddressDocumentRequiredEnglish = "OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
                previousAddressDocumentRequiredWelsh = "OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-EMAIL-WELSH",
                ninoNotMatchedEnglish = "OVERSEAS-NINO-NOT-MATCHED-EMAIL-ENGLISH",
                ninoNotMatchedWelsh = "OVERSEAS-NINO-NOT-MATCHED-EMAIL-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-EMAIL-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-EMAIL-WELSH",
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
                idDocumentRequiredWelsh = "ID-DOCUMENT-REQUIRED-LETTER-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-LETTER-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-LETTER-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            ),
            postal = PostalNotifyLetterTemplateConfiguration(
                rejectedDocumentEnglish = "POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
                rejectedDocumentWelsh = "POSTAL-REJECTED-DOCUMENT-LETTER-WELSH",
                ninoNotMatchedEnglish = "POSTAL-NINO-NOT-MATCHED-LETTER-ENGLISH",
                ninoNotMatchedWelsh = "POSTAL-NINO-NOT-MATCHED-LETTER-WELSH",
                ninoNotMatchedRestrictedDocumentsListEnglish = "POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
                ninoNotMatchedRestrictedDocumentsListWelsh = "POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-WELSH",
                rejectedSignatureEnglish = "POSTAL-REJECTED-SIGNATURE-LETTER-ENGLISH",
                rejectedSignatureWelsh = "POSTAL-REJECTED-SIGNATURE-LETTER-WELSH",
                rejectedSignatureWithReasonsEnglish = "POSTAL-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
                rejectedSignatureWithReasonsWelsh = "POSTAL-REJECTED-SIGNATURE-WITH-REASONS-LETTER-WELSH",
                requestedSignatureEnglish = "POSTAL-REQUESTED-SIGNATURE-LETTER-ENGLISH",
                requestedSignatureWelsh = "POSTAL-REQUESTED-SIGNATURE-LETTER-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-LETTER-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-LETTER-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            ),
            proxy = ProxyNotifyLetterTemplateConfiguration(
                rejectedDocumentEnglish = "PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
                rejectedDocumentWelsh = "PROXY-REJECTED-DOCUMENT-LETTER-WELSH",
                rejectedSignatureEnglish = "PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",
                rejectedSignatureWelsh = "PROXY-REJECTED-SIGNATURE-LETTER-WELSH",
                rejectedSignatureWithReasonsEnglish = "PROXY-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
                rejectedSignatureWithReasonsWelsh = "PROXY-REJECTED-SIGNATURE-WITH-REASONS-LETTER-WELSH",
                ninoNotMatchedEnglish = "PROXY-NINO-NOT-MATCHED-LETTER-ENGLISH",
                ninoNotMatchedWelsh = "PROXY-NINO-NOT-MATCHED-LETTER-WELSH",
                ninoNotMatchedRestrictedDocumentsListEnglish = "PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
                ninoNotMatchedRestrictedDocumentsListWelsh = "PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-WELSH",
                requestedSignatureEnglish = "PROXY-REQUESTED-SIGNATURE-LETTER-ENGLISH",
                requestedSignatureWelsh = "PROXY-REQUESTED-SIGNATURE-LETTER-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-LETTER-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-LETTER-WELSH",
                notRegisteredToVoteEnglish = "NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
                notRegisteredToVoteWelsh = "NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            ),
            overseas = OverseasNotifyLetterTemplateConfiguration(
                rejectedParentGuardianEnglish = "OVERSEAS-REJECTED-PARENT-GUARDIAN-LETTER-ENGLISH",
                rejectedParentGuardianWelsh = "OVERSEAS-REJECTED-PARENT-GUARDIAN-LETTER-WELSH",
                rejectedPreviousAddressEnglish = "OVERSEAS-REJECTED-PREVIOUS-ADDRESS-LETTER-ENGLISH",
                rejectedPreviousAddressWelsh = "OVERSEAS-REJECTED-PREVIOUS-ADDRESS-LETTER-WELSH",
                rejectedDocumentEnglish = "OVERSEAS-REJECTED-DOCUMENT-LETTER-ENGLISH",
                rejectedDocumentWelsh = "OVERSEAS-REJECTED-DOCUMENT-LETTER-WELSH",
                parentGuardianProofRequiredEnglish = "OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-LETTER-ENGLISH",
                parentGuardianProofRequiredWelsh = "OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-LETTER-WELSH",
                previousAddressDocumentRequiredEnglish = "OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-LETTER-ENGLISH",
                previousAddressDocumentRequiredWelsh = "OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-LETTER-WELSH",
                ninoNotMatchedEnglish = "OVERSEAS-NINO-NOT-MATCHED-LETTER-ENGLISH",
                ninoNotMatchedWelsh = "OVERSEAS-NINO-NOT-MATCHED-LETTER-WELSH",
                bespokeCommEnglish = "BESPOKE-COMM-LETTER-ENGLISH",
                bespokeCommWelsh = "BESPOKE-COMM-LETTER-WELSH",
            ),
        ),
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
            "VOTER_CARD,,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "VOTER_CARD,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "POSTAL,,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "POSTAL,,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-LETTER-ENGLISH",
            "POSTAL,,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
            "POSTAL,,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-LETTER-ENGLISH",
            "POSTAL,,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "POSTAL,,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
            "POSTAL,,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "POSTAL,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "PROXY,,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
            "PROXY,,REQUESTED_SIGNATURE, PROXY-REQUESTED-SIGNATURE-LETTER-ENGLISH",
            "PROXY,,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "PROXY,,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
            "PROXY,,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "PROXY,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "OVERSEAS,,REJECTED_PARENT_GUARDIAN,OVERSEAS-REJECTED-PARENT-GUARDIAN-LETTER-ENGLISH",
            "OVERSEAS,,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-LETTER-ENGLISH",
            "OVERSEAS,,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "OVERSEAS,,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "OVERSEAS,,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-LETTER-ENGLISH",
            "OVERSEAS,,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-LETTER-ENGLISH",
            "OVERSEAS,,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_REJECTED, REJECTED-ID-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "VOTER_CARD,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-LETTER-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
            "POSTAL,ENGLISH,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-LETTER-ENGLISH",
            "POSTAL,ENGLISH,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "POSTAL,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
            "POSTAL,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "POSTAL,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "PROXY,ENGLISH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-LETTER-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-LETTER-ENGLISH",
            "PROXY,ENGLISH,REQUESTED_SIGNATURE, PROXY-REQUESTED-SIGNATURE-LETTER-ENGLISH",
            "PROXY,ENGLISH,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "PROXY,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-ENGLISH",
            "PROXY,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",
            "PROXY,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_PARENT_GUARDIAN,OVERSEAS-REJECTED-PARENT-GUARDIAN-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-LETTER-ENGLISH",
            "OVERSEAS,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-LETTER-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_REJECTED, REJECTED-ID-LETTER-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-LETTER-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-LETTER-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-LETTER-WELSH",
            "VOTER_CARD,WELSH,BESPOKE_COMM, BESPOKE-COMM-LETTER-WELSH",
            "VOTER_CARD,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            "POSTAL,WELSH,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-LETTER-WELSH",
            "POSTAL,WELSH,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-LETTER-WELSH",
            "POSTAL,WELSH,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-LETTER-WELSH",
            "POSTAL,WELSH,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-LETTER-WELSH",
            "POSTAL,WELSH,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-LETTER-WELSH",
            "POSTAL,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-WELSH",
            "POSTAL,WELSH,BESPOKE_COMM, BESPOKE-COMM-LETTER-WELSH",
            "POSTAL,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            "PROXY,WELSH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-LETTER-WELSH",
            "PROXY,WELSH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-LETTER-WELSH",
            "PROXY,WELSH,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-LETTER-WELSH",
            "PROXY,WELSH,REQUESTED_SIGNATURE, PROXY-REQUESTED-SIGNATURE-LETTER-WELSH",
            "PROXY,WELSH,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-LETTER-WELSH",
            "PROXY,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-LETTER-WELSH",
            "PROXY,WELSH,BESPOKE_COMM, BESPOKE-COMM-LETTER-WELSH",
            "PROXY,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-LETTER-WELSH",
            "OVERSEAS,WELSH,REJECTED_PARENT_GUARDIAN, OVERSEAS-REJECTED-PARENT-GUARDIAN-LETTER-WELSH",
            "OVERSEAS,WELSH,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-LETTER-WELSH",
            "OVERSEAS,WELSH,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-LETTER-WELSH",
            "OVERSEAS,WELSH,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-LETTER-WELSH",
            "OVERSEAS,WELSH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-LETTER-WELSH",
            "OVERSEAS,WELSH,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-LETTER-WELSH",
            "OVERSEAS,WELSH,BESPOKE_COMM, BESPOKE-COMM-LETTER-WELSH",
        ],
    )
    fun `should map Notification Type in language for letter channel to Notify Template ID`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String,
    ) {
        // Given

        // When
        val notifyTemplateId =
            mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, CommunicationChannel.LETTER, language)

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
            "VOTER_CARD,,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "VOTER_CARD,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "POSTAL,,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            "POSTAL,,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "POSTAL,,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-EMAIL-ENGLISH",
            "POSTAL,,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
            "POSTAL,,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
            "POSTAL,,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "POSTAL,,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
            "POSTAL,,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "POSTAL,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "PROXY,,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",
            "PROXY,,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
            "PROXY,,REQUESTED_SIGNATURE, PROXY-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
            "PROXY,,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "PROXY,,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
            "PROXY,,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "PROXY,,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "OVERSEAS,,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
            "OVERSEAS,,REJECTED_PARENT_GUARDIAN,OVERSEAS-REJECTED-PARENT-GUARDIAN-EMAIL-ENGLISH",
            "OVERSEAS,,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-EMAIL-ENGLISH",
            "OVERSEAS,,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "OVERSEAS,,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "OVERSEAS,,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
            "OVERSEAS,,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-EMAIL-ENGLISH",
            "OVERSEAS,,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",

            "VOTER_CARD,ENGLISH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "VOTER_CARD,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_DOCUMENT, POSTAL-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "POSTAL,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_DOCUMENT, PROXY-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "PROXY,ENGLISH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-ENGLISH",
            "PROXY,ENGLISH,REQUESTED_SIGNATURE, PROXY-REQUESTED-SIGNATURE-EMAIL-ENGLISH",
            "PROXY,ENGLISH,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "PROXY,ENGLISH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-ENGLISH",
            "PROXY,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",
            "PROXY,ENGLISH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_PARENT_GUARDIAN,OVERSEAS-REJECTED-PARENT-GUARDIAN-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-EMAIL-ENGLISH",
            "OVERSEAS,ENGLISH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-ENGLISH",

            "VOTER_CARD,WELSH,APPLICATION_RECEIVED, RECEIVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,APPLICATION_APPROVED, APPROVED-ID-EMAIL-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION, PHOTO-RESUBMISSION-EMAIL-WELSH",
            "VOTER_CARD,WELSH,PHOTO_RESUBMISSION_WITH_REASONS, PHOTO-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION, ID-DOCUMENT-RESUBMISSION-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_RESUBMISSION_WITH_REASONS, ID-DOCUMENT-RESUBMISSION-WITH-REASONS-EMAIL-WELSH",
            "VOTER_CARD,WELSH,ID_DOCUMENT_REQUIRED, ID-DOCUMENT-REQUIRED-EMAIL-WELSH",
            "VOTER_CARD,WELSH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-WELSH",
            "VOTER_CARD,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            "POSTAL,WELSH,APPLICATION_RECEIVED, POSTAL-RECEIVED-ID-EMAIL-WELSH",
            "POSTAL,WELSH,REJECTED_SIGNATURE, POSTAL-REJECTED-SIGNATURE-EMAIL-WELSH",
            "POSTAL,WELSH,REJECTED_SIGNATURE_WITH_REASONS, POSTAL-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-WELSH",
            "POSTAL,WELSH,REQUESTED_SIGNATURE, POSTAL-REQUESTED-SIGNATURE-EMAIL-WELSH",
            "POSTAL,WELSH,NINO_NOT_MATCHED, POSTAL-NINO-NOT-MATCHED-EMAIL-WELSH",
            "POSTAL,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, POSTAL-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-WELSH",
            "POSTAL,WELSH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-WELSH",
            "POSTAL,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            "PROXY,WELSH,APPLICATION_RECEIVED, PROXY-RECEIVED-ID-EMAIL-WELSH",
            "PROXY,WELSH,REJECTED_SIGNATURE, PROXY-REJECTED-SIGNATURE-EMAIL-WELSH",
            "PROXY,WELSH,REJECTED_SIGNATURE_WITH_REASONS, PROXY-REJECTED-SIGNATURE-WITH-REASONS-EMAIL-WELSH",
            "PROXY,WELSH,NINO_NOT_MATCHED, PROXY-NINO-NOT-MATCHED-EMAIL-WELSH",
            "PROXY,WELSH,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST, PROXY-NINO-NOT-MATCHED-RESTRICTED-DOCUMENTS-LIST-EMAIL-WELSH",
            "PROXY,WELSH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-WELSH",
            "PROXY,WELSH,NOT_REGISTERED_TO_VOTE, NOT-REGISTERED-TO-VOTE-EMAIL-WELSH",
            "OVERSEAS,WELSH,APPLICATION_RECEIVED, OVERSEAS-RECEIVED-ID-EMAIL-WELSH",
            "OVERSEAS,WELSH,REJECTED_PARENT_GUARDIAN,OVERSEAS-REJECTED-PARENT-GUARDIAN-EMAIL-WELSH",
            "OVERSEAS,WELSH,REJECTED_PREVIOUS_ADDRESS,OVERSEAS-REJECTED-PREVIOUS-ADDRESS-EMAIL-WELSH",
            "OVERSEAS,WELSH,REJECTED_DOCUMENT,OVERSEAS-REJECTED-DOCUMENT-EMAIL-WELSH",
            "OVERSEAS,WELSH,NINO_NOT_MATCHED, OVERSEAS-NINO-NOT-MATCHED-EMAIL-WELSH",
            "OVERSEAS,WELSH,PREVIOUS_ADDRESS_DOCUMENT_REQUIRED, OVERSEAS-PREVIOUS-ADDRESS-DOCUMENT-REQUIRED-EMAIL-WELSH",
            "OVERSEAS,WELSH,PARENT_GUARDIAN_PROOF_REQUIRED, OVERSEAS-PARENT-GUARDIAN-PROOF-REQUIRED-EMAIL-WELSH",
            "OVERSEAS,WELSH,BESPOKE_COMM, BESPOKE-COMM-EMAIL-WELSH",
        ],
    )
    fun `should map Notification Type in language for email channel to Notify Template ID`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
        expected: String,
    ) {
        // Given

        // When
        val notifyTemplateId =
            mapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, CommunicationChannel.EMAIL, language)

        // Then
        assertThat(notifyTemplateId).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD,,APPLICATION_APPROVED",
            "VOTER_CARD,ENGLISH,APPLICATION_APPROVED",
            "VOTER_CARD,WELSH,APPLICATION_APPROVED",
        ],
    )
    fun `should fail to map Notification Type in language for letter channel for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        notificationType: NotificationType,
    ) {
        // Given

        // When
        val error =
            catchException {
                mapper.fromNotificationTypeForChannelInLanguage(
                    sourceType,
                    notificationType,
                    CommunicationChannel.LETTER,
                    language,
                )
            }

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
        ],
    )
    fun `should fail to map letter Template Type in language for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        channel: CommunicationChannel,
        templateType: NotificationType,
        channelString: String,
    ) {
        // Given

        // When
        val error = catchException {
            mapper.fromNotificationTypeForChannelInLanguage(
                sourceType,
                templateType,
                channel,
                language,
            )
        }

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
        ],
    )
    fun `should fail to map email Template Type in language for unsupported combination`(
        sourceType: SourceType,
        language: LanguageDto?,
        channel: CommunicationChannel,
        templateType: NotificationType,
        channelString: String,
    ) {
        // Given

        // When
        val error = catchException {
            mapper.fromNotificationTypeForChannelInLanguage(
                sourceType,
                templateType,
                channel,
                language,
            )
        }

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
        val error = catchException {
            mapper.fromNotificationTypeForChannelInLanguage(
                sourceType,
                notificationType,
                CommunicationChannel.EMAIL,
                language,
            )
        }

        // Then
        assertThat(error)
            .isInstanceOfAny(NotificationTemplateNotFoundException::class.java)
            .hasMessage("No email template configuration defined for sourceType ANONYMOUS_ELECTOR_DOCUMENT")
    }
}

private fun LanguageDto?.toMessage(): String = if (this == LanguageDto.WELSH) "Welsh" else "English"
