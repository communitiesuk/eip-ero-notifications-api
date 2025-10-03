package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation
import java.time.LocalDate

@Component
class SignatureResubmissionTemplatePreviewDtoMapper {

    @Autowired
    private lateinit var languageMapper: LanguageMapper

    @Autowired
    private lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Autowired
    protected lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    fun signatureResubmissionNotificationType(request: GenerateSignatureResubmissionTemplatePreviewRequest): NotificationType =
        // SIGNATURE_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(request.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrEmpty()) {
                SIGNATURE_RESUBMISSION_WITH_REASONS
            } else {
                SIGNATURE_RESUBMISSION
            }
        }

    fun fromRequestToPersonalisationDto(
        request: GenerateSignatureResubmissionTemplatePreviewRequest,
    ): SignatureResubmissionPersonalisationDto {
        val languageDto = request.language?.let(languageMapper::fromApiToDto) ?: LanguageDto.ENGLISH
        val shortSourceTypeString = sourceTypeMapper.toShortSourceTypeString(request.sourceType, languageDto)
        val fullSourceTypeString = sourceTypeMapper.toFullSourceTypeString(request.sourceType, languageDto)
        val hasRejectionReasons = request.personalisation.rejectionReasonsExcludingOther.isNotEmpty()
        // Template SIGNATURE_RESUBMISSION_WITH_REASONS is only for rejected signatures, and has this text as standard.
        val includeSignatureNotSuitableText = request.personalisation.isRejected && !hasRejectionReasons

        return with(request.personalisation) {
            SignatureResubmissionPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroContactDetails.let(eroContactDetailsMapper::fromApiToDto),
                shortSourceTypeString = shortSourceTypeString,
                fullSourceTypeString = fullSourceTypeString,
                rejectionNotes = rejectionNotes?.ifBlank { null },
                rejectionReasons = mapSignatureRejectionReasons(languageDto, this),
                rejectionFreeText = rejectionFreeText?.ifBlank { null },
                deadline = mapDeadline(deadlineDate, deadlineTime, languageDto, fullSourceTypeString),
                uploadSignatureLink = uploadSignatureLink,
                signatureNotSuitableText = signatureRejectionReasonMapper.toSignatureNotSuitableText(
                    fullSourceTypeString,
                    languageDto,
                    includeSignatureNotSuitableText,
                ),
            )
        }
    }

    fun mapSignatureRejectionReasons(
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

    fun mapDeadline(
        deadlineDate: LocalDate?,
        deadlineTime: String?,
        languageDto: LanguageDto,
        sourceTypeString: String,
    ): String? = deadlineDate?.let {
        deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, languageDto, sourceTypeString)
    }
}
