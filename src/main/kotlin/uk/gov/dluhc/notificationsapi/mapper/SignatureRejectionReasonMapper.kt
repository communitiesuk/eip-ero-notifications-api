package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason

@Component
class SignatureRejectionReasonMapper(private val messageSource: MessageSource) {

    fun toSignatureRejectionReasonString(
        signatureRejectionReason: SignatureRejectionReason,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.signature-rejection.signature-reasons.${signatureRejectionReason.value}",
            null,
            languageDto.locale
        )
    }

    fun toSignatureRejectionReasonString(
        signatureRejectionReason: uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.signature-rejection.signature-reasons.${signatureRejectionReason.value}",
            null,
            languageDto.locale
        )
    }
}
