package uk.gov.dluhc.notificationsapi.mapper

import org.apache.commons.lang3.StringUtils.isNotBlank
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class IdentityDocumentResubmissionTemplatePreviewDtoMapper {

    @Mapping(target = "notificationType", expression = "java( idDocumentResubmissionNotificationType(request) )")
    abstract fun toIdDocumentResubmissionTemplatePreviewDto(
        request: GenerateIdDocumentResubmissionTemplatePreviewRequest,
    ): GenerateIdDocumentResubmissionTemplatePreviewDto

    protected fun idDocumentResubmissionNotificationType(request: GenerateIdDocumentResubmissionTemplatePreviewRequest): NotificationType =
        // ID_DOCUMENT_RESUBMISSION_WITH_REASONS should be used if all rejected documents have either any rejection reasons (excluding OTHER)
        // or has rejection notes
        with(request.personalisation) {
            if (!rejectedDocuments.isNullOrEmpty() &&
                rejectedDocuments.all { it.rejectionReasonsExcludingOther.isNotEmpty() || isNotBlank(it.rejectionNotes) }
            )
                ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            else
                ID_DOCUMENT_RESUBMISSION
        }
}
