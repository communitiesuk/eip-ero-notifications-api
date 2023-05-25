package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessaging
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation as IdDocumentPersonalisationMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApi
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation as IdDocumentPersonalisationApi

/**
 * Class to map rejected documents and their reasons into a snippet of markdown that can subsequently be used to pass
 * into a personalisation field for inclusion in a gov.uk notify template.
 *
 * This is necessary because gov.uk notify templating language is not rich enough to be able to perform the logic and mapping
 * that is required by the business.
 */
@Component
class IdentityDocumentResubmissionDocumentRejectionTextMapper(
    private val rejectedDocumentReasonMapper: RejectedDocumentReasonMapper,
) {

    companion object {
        private val REJECTED_DOCUMENT_MARKDOWN_TEMPLATE = """
            <document-type>

            <bulleted-list-of-reasons>

            <rejection-reason-notes>

            ----
            
        """.trimIndent()
    }

    fun toDocumentRejectionText(language: LanguageDto, personalisation: IdDocumentPersonalisationApi): String? {
        return personalisation.rejectedDocuments?.joinToString("\n") { rejectedDocument ->
            REJECTED_DOCUMENT_MARKDOWN_TEMPLATE
                .replaceApiDocumentType(rejectedDocument.documentType)
                .replaceListOfApiReasons(rejectedDocument.rejectionReasons, language)
                .replaceRejectionReasonNotes(rejectedDocument.rejectionNotes)
        }?.plus("\n")
    }

    fun toDocumentRejectionText(language: LanguageDto, personalisation: IdDocumentPersonalisationMessaging): String? {
        return personalisation.rejectedDocuments?.joinToString("\n") { rejectedDocument ->
            REJECTED_DOCUMENT_MARKDOWN_TEMPLATE
                .replaceMessagingDocumentType(rejectedDocument.documentType)
                .replaceListOfMessagingReasons(rejectedDocument.rejectionReasons, language)
                .replaceRejectionReasonNotes(rejectedDocument.rejectionNotes)
        }?.plus("\n")
    }

    private fun String.replaceApiDocumentType(documentType: DocumentTypeApi): String =
        replace(
            "<document-type>",
            documentType.toString()
        )

    private fun String.replaceListOfApiReasons(
        documentRejectionReasons: List<DocumentRejectionReasonApi>,
        language: LanguageDto,
    ): String =
        replace(
            "<bulleted-list-of-reasons>\n\n",
            if (documentRejectionReasons.isNotEmpty()) {
                documentRejectionReasons.joinToString("\n") {
                    "* ${rejectedDocumentReasonMapper.toDocumentRejectionReasonString(it, language)}"
                }.plus("\n\n")
            } else {
                ""
            }
        )

    private fun String.replaceRejectionReasonNotes(rejectionNotes: String?): String =
        replace(
            "<rejection-reason-notes>\n\n",
            if (!rejectionNotes.isNullOrBlank()) {
                rejectionNotes
                    .plus("\n\n")
            } else {
                ""
            }
        )

    private fun String.replaceMessagingDocumentType(documentType: DocumentTypeMessaging): String =
        replace(
            "<document-type>",
            documentType.toString()
        )

    private fun String.replaceListOfMessagingReasons(
        documentRejectionReasons: List<DocumentRejectionReasonMessaging>,
        language: LanguageDto,
    ): String =
        replace(
            "<bulleted-list-of-reasons>\n\n",
            if (documentRejectionReasons.isNotEmpty()) {
                documentRejectionReasons.joinToString("\n") {
                    "* ${rejectedDocumentReasonMapper.toDocumentRejectionReasonString(it, language)}"
                }.plus("\n\n")
            } else {
                ""
            }
        )
}
