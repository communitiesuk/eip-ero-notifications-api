package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.mapToPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.mapper.rejectionReasonsExcludingOther
import uk.gov.dluhc.notificationsapi.utils.getSafeValue
import java.time.LocalDate
import uk.gov.dluhc.notificationsapi.messaging.models.Language as LanguageMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason as SignatureRejectionReasonMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureResubmissionPersonalisation as SignatureResubmissionPersonalisationMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessage
import uk.gov.dluhc.notificationsapi.models.Language as LanguageApi
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason as SignatureRejectionReasonApi
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation as SignatureResubmissionPersonalisationApi
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApi

@Component
class SignatureResubmissionPersonalisationMapper {

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

    fun fromMessagePersonalisationToPersonalisationMap(
        personalisation: SignatureResubmissionPersonalisationMessage,
        sourceType: SourceTypeMessage,
        language: LanguageMessage?,
    ): Map<String, Any> {
        val personalisationMap = mutableMapOf<String, Any>()

        val languageDto = language?.let(languageMapper::fromMessageToDto) ?: LanguageDto.ENGLISH
        val shortSourceTypeString = sourceTypeMapper.toShortSourceTypeString(sourceType, languageDto)
        val fullSourceTypeString = sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto)
        val hasRejectionReasons = personalisation.rejectionReasonsExcludingOther.isNotEmpty()
        // Template SIGNATURE_RESUBMISSION_WITH_REASONS is only for rejected signatures, and has this text as standard.
        val includeSignatureNotSuitableText = personalisation.isRejected && !hasRejectionReasons

        with(personalisation) {
            personalisationMap["applicationReference"] = applicationReference
            personalisationMap["firstName"] = firstName
            personalisationMap["rejectionNotes"] = rejectionNotes.getSafeValue()
            personalisationMap["rejectionReasons"] = mapMessageSignatureRejectionReasons(languageDto, this.rejectionReasonsExcludingOther)
            personalisationMap["rejectionFreeText"] = rejectionFreeText.getSafeValue()
            with(mutableMapOf<String, String>()) {
                eroContactDetails.let(eroContactDetailsMapper::fromSqsToDto).mapToPersonalisation(this)
                personalisationMap.putAll(this)
            }
            personalisationMap["fullSourceType"] = fullSourceTypeString
            personalisationMap["shortSourceType"] = shortSourceTypeString
            personalisationMap["deadline"] = mapDeadline(deadlineDate, deadlineTime, languageDto, fullSourceTypeString).getSafeValue()
            personalisationMap["uploadSignatureLink"] = uploadSignatureLink
            personalisationMap["signatureNotSuitableText"] = signatureRejectionReasonMapper.toSignatureNotSuitableText(
                fullSourceTypeString,
                languageDto,
                includeSignatureNotSuitableText,
            ).getSafeValue()
        }

        return personalisationMap
    }

    fun fromApiPersonalisationToPersonalisationMap(
        personalisation: SignatureResubmissionPersonalisationApi,
        sourceType: SourceTypeApi,
        language: LanguageApi?,
    ): Map<String, Any> {
        val personalisationMap = mutableMapOf<String, Any>()

        val languageDto = language?.let(languageMapper::fromApiToDto) ?: LanguageDto.ENGLISH
        val shortSourceTypeString = sourceTypeMapper.toShortSourceTypeString(sourceType, languageDto)
        val fullSourceTypeString = sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto)
        val hasRejectionReasons = personalisation.rejectionReasonsExcludingOther.isNotEmpty()
        // Template SIGNATURE_RESUBMISSION_WITH_REASONS is only for rejected signatures, and has this text as standard.
        val includeSignatureNotSuitableText = personalisation.isRejected && !hasRejectionReasons

        with(personalisation) {
            personalisationMap["applicationReference"] = applicationReference
            personalisationMap["firstName"] = firstName
            personalisationMap["rejectionNotes"] = rejectionNotes.getSafeValue()
            personalisationMap["rejectionReasons"] = mapApiSignatureRejectionReasons(languageDto, this.rejectionReasonsExcludingOther)
            personalisationMap["rejectionFreeText"] = rejectionFreeText.getSafeValue()
            with(mutableMapOf<String, String>()) {
                eroContactDetails.let(eroContactDetailsMapper::fromApiToDto).mapToPersonalisation(this)
                personalisationMap.putAll(this)
            }
            personalisationMap["fullSourceType"] = fullSourceTypeString
            personalisationMap["shortSourceType"] = shortSourceTypeString
            personalisationMap["deadline"] = mapDeadline(deadlineDate, deadlineTime, languageDto, fullSourceTypeString).getSafeValue()
            personalisationMap["uploadSignatureLink"] = uploadSignatureLink
            personalisationMap["signatureNotSuitableText"] = signatureRejectionReasonMapper.toSignatureNotSuitableText(
                fullSourceTypeString,
                languageDto,
                includeSignatureNotSuitableText,
            ).getSafeValue()
        }

        return personalisationMap
    }

    fun mapMessageSignatureRejectionReasons(
        languageDto: LanguageDto,
        reasons: List<SignatureRejectionReasonMessage>,
    ): List<String> {
        return reasons.map { reason ->
            signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                reason,
                languageDto,
            )
        }
    }

    fun mapApiSignatureRejectionReasons(
        languageDto: LanguageDto,
        reasons: List<SignatureRejectionReasonApi>,
    ): List<String> {
        return reasons.map { reason ->
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
