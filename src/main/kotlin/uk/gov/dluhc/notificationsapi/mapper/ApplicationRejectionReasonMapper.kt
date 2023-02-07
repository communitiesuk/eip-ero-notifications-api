package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale.ENGLISH
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectionReason as ApplicationRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason as ApplicationRejectionReasonApiEnum

@Component
class ApplicationRejectionReasonMapper(private val messageSource: MessageSource) {

    fun toApplicationRejectionReasonString(applicationRejectionReason: ApplicationRejectionReasonApiEnum): String {
        return messageSource.getMessage(
            "templates.application-rejection.rejection-reasons.${applicationRejectionReason.value}",
            null,
            ENGLISH
        )
    }

    fun toApplicationRejectionReasonString(applicationRejectionReason: ApplicationRejectionReasonMessageEnum): String {
        return messageSource.getMessage(
            "templates.application-rejection.rejection-reasons.${applicationRejectionReason.value}",
            null,
            ENGLISH
        )
    }
}
