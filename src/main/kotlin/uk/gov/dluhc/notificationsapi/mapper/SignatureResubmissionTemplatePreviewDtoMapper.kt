package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
@Component
class SignatureResubmissionTemplatePreviewDtoMapper {

    fun signatureResubmissionNotificationType(request: GenerateSignatureResubmissionTemplatePreviewRequest): NotificationType =
        // SIGNATURE_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(request.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrEmpty()) {
                SIGNATURE_RESUBMISSION_WITH_REASONS
            } else {
                SIGNATURE_RESUBMISSION
            }
        }
}
