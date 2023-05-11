package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason

@Component
class RejectedDocumentReasonMapper(private val messageSource: MessageSource) {

    fun toDocumentRejectionReasonString(
        documentRejectionReason: DocumentRejectionReason,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.rejection-reasons.${documentRejectionReason.value}",
            null,
            languageDto.locale
        )
    }
}
