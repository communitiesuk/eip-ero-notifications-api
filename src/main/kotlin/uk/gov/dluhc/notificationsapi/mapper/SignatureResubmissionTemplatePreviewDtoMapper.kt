package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.GenerateSignatureResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation
import java.time.LocalDate

@Mapper(uses = [LanguageMapper::class, CommunicationChannelMapper::class, SourceTypeMapper::class])
abstract class SignatureResubmissionTemplatePreviewDtoMapper {

    @Autowired
    private lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    protected lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Mapping(target = "notificationType", expression = "java( signatureResubmissionNotificationType(request) )")
    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation() ) )",
    )
    abstract fun toSignatureResubmissionTemplatePreviewDto(
        request: GenerateSignatureResubmissionTemplatePreviewRequest,
    ): GenerateSignatureResubmissionTemplatePreviewDto

    protected fun signatureResubmissionNotificationType(request: GenerateSignatureResubmissionTemplatePreviewRequest): NotificationType =
        // SIGNATURE_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(request.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrEmpty()) {
                SIGNATURE_RESUBMISSION_WITH_REASONS
            } else {
                SIGNATURE_RESUBMISSION
            }
        }

    @Mapping(
        target = "signatureRejectionReasons",
        expression = "java( mapSignatureRejectionReasons( languageDto, personalisation ) )",
    )
    @Mapping(
        target = "deadline",
        expression = "java( mapDeadline( personalisation.getDeadlineDate(), personalisation.getDeadlineTime(), languageDto, sourceTypeMapper.toFullSourceTypeString( sourceType, languageDto ) ) )",
    )
    protected abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: SignatureResubmissionPersonalisation,
    ): SignatureResubmissionPersonalisationDto

    protected fun mapSignatureRejectionReasons(
        languageDto: LanguageDto,
        personalisation: SignatureResubmissionPersonalisation,
    ): List<String> {
        return personalisation.rejectionReasonsExcludingOther.map { reason ->
            signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                reason,
                languageDto,
            )
        }
    }

    protected fun mapDeadline(
        deadlineDate: LocalDate?,
        deadlineTime: String?,
        languageDto: LanguageDto,
        sourceTypeString: String,
    ): String? = deadlineDate?.let {
        deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, languageDto, sourceTypeString)
    }
}
