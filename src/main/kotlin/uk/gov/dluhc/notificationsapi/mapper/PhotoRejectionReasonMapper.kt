package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoRejectionReason as PhotoRejectionReasonMessageEnum
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason as PhotoRejectionReasonApiEnum

@Component
class PhotoRejectionReasonMapper(private val messageSource: MessageSource) {

    fun toPhotoRejectionReasonString(
        photoRejectionReason: PhotoRejectionReasonApiEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.photo-rejection.rejection-reasons.${photoRejectionReason.value}",
            null,
            languageDto.locale
        )
    }

    fun toPhotoRejectionReasonString(
        photoRejectionReason: PhotoRejectionReasonMessageEnum,
        languageDto: LanguageDto
    ): String {
        return messageSource.getMessage(
            "templates.photo-rejection.rejection-reasons.${photoRejectionReason.value}",
            null,
            languageDto.locale
        )
    }
}
