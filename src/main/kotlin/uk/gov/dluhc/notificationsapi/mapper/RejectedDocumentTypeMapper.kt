package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApi

@Component
class RejectedDocumentTypeMapper(private val messageSource: MessageSource) {

    fun fromApiToString(documentType: DocumentTypeApi, languageDto: LanguageDto) =
        getMessage(documentType.value, languageDto)

    fun fromMessagingToString(documentType: DocumentTypeMessaging, languageDto: LanguageDto) =
        getMessage(documentType.value, languageDto)

    private fun getMessage(value: String, languageDto: LanguageDto) =
        messageSource.getMessage("templates.document-rejection.document-types.$value", null, languageDto.locale)
}
