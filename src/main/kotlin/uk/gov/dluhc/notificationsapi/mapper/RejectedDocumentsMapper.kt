package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.mapper.rejectionReasonsExcludingOther
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument as RejectedDocumentMessaging
import uk.gov.dluhc.notificationsapi.models.RejectedDocument as RejectedDocumentApi

@Component
class RejectedDocumentsMapper(
    private val rejectedDocumentReasonMapper: RejectedDocumentReasonMapper,
    private val rejectedDocumentTypeMapper: RejectedDocumentTypeMapper
) {
    private val subItemSeparator = "\n  * "

    fun mapRejectionDocumentsFromApi(
        languageDto: LanguageDto,
        documents: List<RejectedDocumentApi>
    ): List<String> {
        return documents.map { document ->
            val docType = rejectedDocumentTypeMapper.toDocumentTypeString(document.documentType, languageDto)
            val docReason = document.rejectionReasonsExcludingOther
                .combineReasons(languageDto, rejectedDocumentReasonMapper::toDocumentRejectionReasonString)
            docType
                .appendIfNotNullOrEmpty(docReason)
                .appendIfNotNullOrEmpty(document.rejectionNotes)
        }
    }

    fun mapRejectionDocumentsFromMessaging(
        languageDto: LanguageDto,
        documents: List<RejectedDocumentMessaging>
    ): List<String> {
        return documents.map { document ->
            val docType = rejectedDocumentTypeMapper.toDocumentTypeString(document.documentType, languageDto)
            val docReason = document.rejectionReasonsExcludingOther
                .combineReasons(languageDto, rejectedDocumentReasonMapper::toDocumentRejectionReasonString)
            docType
                .appendIfNotNullOrEmpty(docReason)
                .appendIfNotNullOrEmpty(document.rejectionNotes)
        }
    }

    private fun <T> List<T>.combineReasons(language: LanguageDto, mapper: (T, LanguageDto) -> String) =
        this.joinToString(separator = subItemSeparator) { mapper.invoke(it, language) }

    private fun String.appendIfNotNullOrEmpty(value: String?) =
        this + ((subItemSeparator + value).takeIf { !value.isNullOrEmpty() } ?: "")
}
