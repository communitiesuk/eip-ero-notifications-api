package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessagingEnum
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApiEnum

@Component
class RejectedDocumentTypeMapper(private val messageSource: MessageSource) {

    fun toDocumentTypeString(
        documentType: DocumentTypeApiEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.document-types.${documentType.value}",
            null,
            languageDto.locale
        )
    }

    fun toDocumentTypeString(
        documentType: DocumentTypeMessagingEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.document-rejection.document-types.${documentType.value}",
            null,
            languageDto.locale
        )
    }
}
