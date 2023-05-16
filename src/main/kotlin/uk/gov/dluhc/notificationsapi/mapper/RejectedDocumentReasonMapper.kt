package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi

@Component
class RejectedDocumentReasonMapper(private val messageSource: MessageSource) {

    fun fromApiToString(documentRejectionReason: DocumentRejectionReasonApi, languageDto: LanguageDto) =
        getMessage(documentRejectionReason.value, languageDto)

    fun fromMessagingToString(documentRejectionReason: DocumentRejectionReasonMessaging, languageDto: LanguageDto) =
        getMessage(documentRejectionReason.value, languageDto)

    private fun getMessage(value: String, languageDto: LanguageDto) =
        messageSource.getMessage("templates.document-rejection.rejection-reasons.$value", null, languageDto.locale)
}
