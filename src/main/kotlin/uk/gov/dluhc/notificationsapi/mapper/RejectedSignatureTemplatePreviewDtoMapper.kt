package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class RejectedSignatureTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Mapping(
        target = "notificationType",
        expression = "java( rejectedSignatureNotificationType(request) )"
    )
    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )"
    )
    abstract fun toRejectedSignatureTemplatePreviewDto(
        request: GenerateRejectedSignatureTemplatePreviewRequest
    ): RejectedSignatureTemplatePreviewDto

    protected fun rejectedSignatureNotificationType(request: GenerateRejectedSignatureTemplatePreviewRequest): NotificationType =
        // REJECTED_SIGNATURE_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(request.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrBlank())
                NotificationType.REJECTED_SIGNATURE_WITH_REASONS
            else
                NotificationType.REJECTED_SIGNATURE
        }

    @Mapping(
        target = "sourceType",
        expression = "java( sourceTypeMapper.toSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        target = "rejectionReasons",
        expression = "java( mapSignatureRejectionReasons( languageDto, personalisation ) )"
    )
    protected abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: RejectedSignaturePersonalisation,
        sourceType: SourceType,
    ): RejectedSignaturePersonalisationDto

    protected fun mapSignatureRejectionReasons(
        languageDto: LanguageDto,
        personalisation: RejectedSignaturePersonalisation
    ): List<String> {
        return personalisation.rejectionReasonsExcludingOther.map { reason ->
            signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                reason,
                languageDto
            )
        }
    }
}
