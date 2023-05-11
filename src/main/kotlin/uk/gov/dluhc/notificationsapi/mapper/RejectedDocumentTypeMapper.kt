package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.DocumentType

@Component
class RejectedDocumentTypeMapper(private val messageSource: MessageSource) {

    fun toDocumentTypeString(
        documentType: DocumentType,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.document-types.${documentType.value}",
            null,
            languageDto.locale
        )
    }
}
