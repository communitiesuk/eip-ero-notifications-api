package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument as RejectedDocumentMessage
import uk.gov.dluhc.notificationsapi.models.RejectedDocument as RejectedDocumentApi

@Component
class RejectedDocumentMapper(
    private val rejectedDocumentReasonMapper: RejectedDocumentReasonMapper,
    private val rejectedDocumentTypeMapper: RejectedDocumentTypeMapper
) {

    fun fromApiRejectedDocumentToString(
        languageDto: LanguageDto,
        document: RejectedDocumentApi
    ): String {
        val docType = rejectedDocumentTypeMapper.fromApiToString(document.documentType, languageDto)
        val docReason = document.rejectionReason?.let { rejectedDocumentReasonMapper.fromApiToString(it, languageDto) }
        return docType.appendIfNotNull(docReason).appendIfNotNull(document.rejectionNotes)
    }

    fun fromMessagingRejectedDocumentToString(
        languageDto: LanguageDto,
        document: RejectedDocumentMessage
    ): String {
        val docType = rejectedDocumentTypeMapper.fromMessagingToString(document.documentType, languageDto)
        val docReason = document.rejectionReason?.let { rejectedDocumentReasonMapper.fromMessagingToString(it, languageDto) }
        return docType.appendIfNotNull(docReason).appendIfNotNull(document.rejectionNotes)
    }

    private fun String.appendIfNotNull(value: String?) = this + (" - $value".takeIf { value != null } ?: "")
}
