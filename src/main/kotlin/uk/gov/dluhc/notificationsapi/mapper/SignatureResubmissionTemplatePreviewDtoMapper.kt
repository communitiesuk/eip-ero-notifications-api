package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.*
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS
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

    @Autowired
    protected lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    fun toSignatureResubmissionPersonalisation(
        request: GenerateSignatureResubmissionTemplatePreviewRequest,
        commonTemplatePreviewDto: CommonTemplatePreviewDto,
    ): Map<String, Any> {
        val personalisationMap = mutableMapOf<String, Any>()

        with(request.personalisation) {
            personalisationMap["applicationReference"] = applicationReference
            personalisationMap["firstName"] = firstName
            personalisationMap["rejectionNotes"] = templatePersonalisationDtoMapper.getSafeValue(rejectionNotes?.ifBlank { null })
            personalisationMap["rejectionReasons"] = mapSignatureRejectionReasons(commonTemplatePreviewDto.language, this)
            personalisationMap["rejectionFreeText"] = templatePersonalisationDtoMapper.getSafeValue(rejectionFreeText?.ifBlank { null })
            with(mutableMapOf<String, String>()) {
                (eroContactDetails.let(eroContactDetailsMapper::fromApiToDto)).mapEroContactFields(this)
                personalisationMap.putAll(this)
            }
            personalisationMap["sourceType"] = sourceTypeMapper.toSourceTypeString(request.sourceType, commonTemplatePreviewDto.language)

            personalisationMap["deadline"] = templatePersonalisationDtoMapper.getSafeValue(mapDeadline(
                deadlineDate,
                deadlineTime,
                commonTemplatePreviewDto.language,
                sourceTypeMapper.toFullSourceTypeString(request.sourceType, commonTemplatePreviewDto.language)
            ))
            personalisationMap["uploadSignatureLink"] = uploadSignatureLink
        }

        return personalisationMap
    }

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
