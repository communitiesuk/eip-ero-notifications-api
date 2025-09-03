package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.GenerateSignatureResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import java.time.LocalDate

@Component
class SignatureResubmissionTemplatePreviewDtoMapper {

    @Autowired
    private lateinit var languageMapper: LanguageMapper

    @Autowired
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Autowired
    private lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Autowired
    protected lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    fun toSignatureResubmissionTemplatePreviewDto(
        request: GenerateSignatureResubmissionTemplatePreviewRequest,
    ): GenerateSignatureResubmissionTemplatePreviewDto {
        val language = request.language!!.let(languageMapper::fromApiToDto)

        return with(request) {
            GenerateSignatureResubmissionTemplatePreviewDto(
                sourceType = sourceType.let(sourceTypeMapper::fromApiToDto),
                channel = channel.let(communicationChannelMapper::fromApiToDto),
                language = language,
                notificationType = signatureResubmissionNotificationType(request),
                personalisation = mapPersonalisation(
                    languageDto = language,
                    personalisation = personalisation,
                    sourceType = sourceType,
                ),
            )
        }
    }

    fun signatureResubmissionNotificationType(request: GenerateSignatureResubmissionTemplatePreviewRequest): NotificationType =
        // SIGNATURE_RESUBMISSION_WITH_REASONS should be used if there are rejection reasons (excluding OTHER) or there are rejection notes
        with(request.personalisation) {
            if (rejectionReasonsExcludingOther.isNotEmpty() || !rejectionNotes.isNullOrEmpty()) {
                SIGNATURE_RESUBMISSION_WITH_REASONS
            } else {
                SIGNATURE_RESUBMISSION
            }
        }

    fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: SignatureResubmissionPersonalisation,
        sourceType: SourceType,
    ): SignatureResubmissionPersonalisationDto = with(personalisation) {
        SignatureResubmissionPersonalisationDto(
            applicationReference = applicationReference,
            firstName = firstName,
            eroContactDetails = eroContactDetails.let(eroContactDetailsMapper::fromApiToDto),
            personalisationSourceTypeString = sourceTypeMapper.toSourceTypeString(sourceType, languageDto),
            rejectionNotes = rejectionNotes?.ifBlank { null },
            rejectionReasons = mapSignatureRejectionReasons(languageDto, this),
            rejectionFreeText = rejectionFreeText?.ifBlank { null },
            deadline = mapDeadline(deadlineDate, deadlineTime, languageDto, sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto)),
            uploadSignatureLink = uploadSignatureLink,
        )
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
