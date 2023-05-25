package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi

@Component
class RejectedDocumentReasonMapper(private val messageSource: MessageSource) {

    fun toDocumentRejectionReasonString(
        documentRejectionReason: DocumentRejectionReasonApi,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.rejection-reasons.${documentRejectionReason.value}",
            null,
            languageDto.locale
        )
    }

    fun toDocumentRejectionReasonString(
        documentRejectionReason: DocumentRejectionReasonMessaging,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.rejection-reasons.${documentRejectionReason.value}",
            null,
            languageDto.locale
        )
    }
}
