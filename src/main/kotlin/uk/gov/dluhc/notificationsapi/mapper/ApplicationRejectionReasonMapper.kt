package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason
import java.util.Locale.ENGLISH

@Component
class ApplicationRejectionReasonMapper(private val messageSource: MessageSource) {

    fun toApplicationRejectionReasonMessage(applicationRejectionReason: ApplicationRejectionReason): String {
        return messageSource.getMessage(
            "templates.application-rejection.rejection-reasons.${applicationRejectionReason.value}",
            null,
            ENGLISH
        )
    }
}
