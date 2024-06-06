package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.NotificationChannel

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class IdentityDocumentResubmissionTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Mapping(target = "notificationType", expression = "java( idDocumentResubmissionNotificationType(request) )")
    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getChannel() ) )",
    )
    abstract fun toIdDocumentResubmissionTemplatePreviewDto(
        request: GenerateIdDocumentResubmissionTemplatePreviewRequest,
    ): GenerateIdDocumentResubmissionTemplatePreviewDto

    protected fun idDocumentResubmissionNotificationType(request: GenerateIdDocumentResubmissionTemplatePreviewRequest): NotificationType =
        // ID_DOCUMENT_RESUBMISSION_WITH_REASONS should be used if any rejected documents have either any rejection reasons (excluding OTHER)
        // or has rejection notes
        with(request.personalisation) {
            if (!rejectedDocuments.isNullOrEmpty() &&
                rejectedDocuments.any { it.rejectionReasonsExcludingOther.isNotEmpty() || !it.rejectionNotes.isNullOrBlank() }
            ) {
                ID_DOCUMENT_RESUBMISSION_WITH_REASONS
            } else {
                ID_DOCUMENT_RESUBMISSION
            }
        }

    @Mapping(
        target = "documentRejectionText",
        expression = "java( documentRejectionTextMapper.toDocumentRejectionText( languageDto, personalisation, channel ) )",
    )
    protected abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: IdDocumentPersonalisation,
        channel: NotificationChannel,
    ): IdDocumentPersonalisationDto
}
