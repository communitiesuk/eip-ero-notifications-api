package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason as ApplicationRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason as ApplicationRejectionReasonApiEnum

@Component
class ApplicationRejectionReasonMapper(private val messageSource: MessageSource) {

    fun toApplicationRejectionReasonString(
        applicationRejectionReason: ApplicationRejectionReasonApiEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.application-rejection.rejection-reasons.${applicationRejectionReason.value}",
            null,
            languageDto.locale
        )
    }

    fun toApplicationRejectionReasonString(
        applicationRejectionReason: ApplicationRejectionReasonMessageEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.application-rejection.rejection-reasons.${applicationRejectionReason.value}",
            null,
            languageDto.locale
        )
    }
}
