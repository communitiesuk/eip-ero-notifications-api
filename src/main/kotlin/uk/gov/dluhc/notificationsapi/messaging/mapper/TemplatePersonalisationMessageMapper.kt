package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVotePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.DeadlineMapper
import uk.gov.dluhc.notificationsapi.mapper.EroContactDetailsMapper
import uk.gov.dluhc.notificationsapi.mapper.IdentityDocumentResubmissionDocumentRejectionTextMapper
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.PhotoRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentsMapper
import uk.gov.dluhc.notificationsapi.mapper.SignatureRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.NinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.NotRegisteredToVotePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RequestedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import java.time.LocalDate

@Mapper
abstract class TemplatePersonalisationMessageMapper {

    @Autowired
    protected lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Autowired
    protected lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Autowired
    protected lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Autowired
    protected lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Autowired
    protected lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Autowired
    protected lateinit var sourceTypeMapper: SourceTypeMapper

    @Autowired
    protected lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    protected lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Autowired
    private lateinit var languageMapper: LanguageMapper

    @Mapping(
        target = "photoRejectionReasons",
        expression = "java( mapPhotoRejectionReasons( languageDto, personalisationMessage ) )",
    )
    abstract fun toPhotoPersonalisationDto(
        personalisationMessage: PhotoPersonalisation,
        languageDto: LanguageDto,
    ): PhotoPersonalisationDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    @Mapping(
        target = "rejectionReasons",
        expression = "java( mapSignatureRejectionReasons( languageDto, personalisationMessage.getRejectionReasons() ) )",
    )
    abstract fun toRejectedSignaturePersonalisationDto(
        personalisationMessage: RejectedSignaturePersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): RejectedSignaturePersonalisationDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    abstract fun toRequestedSignaturePersonalisationDto(
        personalisationMessage: RequestedSignaturePersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): RequestedSignaturePersonalisationDto

    @Mapping(
        target = "documentRejectionText",
        expression = "java( mapDocumentRejectionText( languageDto, personalisationMessage, channel ) )",
    )
    abstract fun toIdDocumentPersonalisationDto(
        personalisationMessage: IdDocumentPersonalisation,
        languageDto: LanguageDto,
        channel: CommunicationChannel,
    ): IdDocumentPersonalisationDto

    abstract fun toIdDocumentRequiredPersonalisationDto(personalisationMessage: IdDocumentRequiredPersonalisation): IdDocumentRequiredPersonalisationDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    abstract fun toReceivedPersonalisationDto(
        personalisationMessage: BasePersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): ApplicationReceivedPersonalisationDto

    abstract fun toApprovedPersonalisationDto(personalisationMessage: BasePersonalisation): ApplicationApprovedPersonalisationDto

    @Mapping(
        target = "rejectionReasonList",
        expression = "java( mapApplicationRejectionReasons( languageDto, personalisationMessage ) )",
    )
    abstract fun toRejectedPersonalisationDto(
        personalisationMessage: ApplicationRejectedPersonalisation,
        languageDto: LanguageDto,
    ): ApplicationRejectedPersonalisationDto

    protected fun mapApplicationRejectionReasons(
        languageDto: LanguageDto,
        personalisationMessage: ApplicationRejectedPersonalisation,
    ): List<String> {
        return personalisationMessage.rejectionReasonList.map { reason ->
            applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                reason,
                languageDto,
            )
        }
    }

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    @Mapping(
        target = "documents",
        expression = "java( rejectedDocumentsMapper.mapRejectionDocumentsFromMessaging(languageDto, personalisation.getDocuments()) )",
    )
    @Mapping(target = "rejectedDocumentFreeText", source = "personalisation.rejectedDocumentMessage")
    abstract fun toRejectedDocumentPersonalisationDto(
        personalisation: RejectedDocumentPersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): RejectedDocumentPersonalisationDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    @Mapping(target = "additionalNotes", source = "personalisation.additionalNotes")
    abstract fun toRequiredDocumentTemplatePersonalisationDto(
        personalisation: NinoNotMatchedPersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): RequiredDocumentPersonalisationDto

    @Mapping(
        target = "personalisationFullSourceTypeString",
        expression = "java( mapFullSourceType( languageDto, sourceType ) )",
    )
    @Mapping(target = "subjectHeader", source = "personalisation.subjectHeader")
    @Mapping(target = "details", source = "personalisation.details")
    @Mapping(target = "whatToDo", source = "personalisation.whatToDo")
    @Mapping(
        target = "deadline",
        expression = "java( mapDeadline( personalisation.getDeadlineDate(), personalisation.getDeadlineTime(), languageDto, sourceType ) )",
    )
    abstract fun toBespokeCommTemplatePersonalisationDto(
        personalisation: BespokeCommPersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): BespokeCommPersonalisationDto

    @Mapping(
        target = "personalisationFullSourceTypeString",
        expression = "java( mapFullSourceType( languageDto, sourceType ) )",
    )
    @Mapping(target = "freeText", source = "personalisation.freeText")
    @Mapping(target = "property", source = "personalisation.property")
    @Mapping(target = "street", source = "personalisation.street")
    @Mapping(target = "town", source = "personalisation.town")
    @Mapping(target = "area", source = "personalisation.area")
    @Mapping(target = "locality", source = "personalisation.locality")
    @Mapping(target = "postcode", source = "personalisation.postcode")
    @Mapping(
        target = "deadline",
        expression = "java( mapDeadline( personalisation.getDeadlineDate(), personalisation.getDeadlineTime(), languageDto, sourceType ) )",
    )
    abstract fun toNotRegisteredToVoteTemplatePersonalisationDto(
        personalisation: NotRegisteredToVotePersonalisation,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): NotRegisteredToVotePersonalisationDto

    fun toSignatureResubmissionTemplatePersonalisationDto(
        personalisation: SignatureResubmissionPersonalisation,
        language: Language,
        sourceType: SourceType,
    ): SignatureResubmissionPersonalisationDto {
        val languageDto = language.let(languageMapper::fromMessageToDto)
        val shortSourceTypeString = sourceTypeMapper.toShortSourceTypeString(sourceType, languageDto)
        val fullSourceTypeString = sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto)
        val hasRejectionReasons = personalisation.rejectionReasonsExcludingOther.isNotEmpty()
        // Template SIGNATURE_RESUBMISSION_WITH_REASONS is only for rejected signatures, and has this text as standard.
        val includeSignatureNotSuitableText = personalisation.isRejected && !hasRejectionReasons

        return with(personalisation) {
            SignatureResubmissionPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroContactDetails.let(eroContactDetailsMapper::fromSqsToDto),
                shortSourceTypeString = shortSourceTypeString,
                fullSourceTypeString = fullSourceTypeString,
                rejectionNotes = rejectionNotes?.ifBlank { null },
                rejectionReasons = mapSignatureRejectionReasons(languageDto, this.rejectionReasons),
                rejectionFreeText = rejectionFreeText?.ifBlank { null },
                deadline = mapDeadline(deadlineDate, deadlineTime, languageDto, sourceType),
                uploadSignatureLink = uploadSignatureLink,
                signatureNotSuitableText = mapSignatureNotSuitableText(fullSourceTypeString, includeSignatureNotSuitableText, languageDto),
            )
        }
    }

    protected fun mapSourceType(
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): String = sourceTypeMapper.toSourceTypeString(sourceType, languageDto)

    protected fun mapFullSourceType(
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): String = sourceTypeMapper.toFullSourceTypeString(sourceType, languageDto)

    protected fun mapDeadline(
        deadlineDate: LocalDate?,
        deadlineTime: String?,
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): String? {
        return if (deadlineDate != null) {
            deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, languageDto, mapFullSourceType(languageDto, sourceType))
        } else {
            null
        }
    }

    protected fun mapPhotoRejectionReasons(
        languageDto: LanguageDto,
        personalisation: PhotoPersonalisation,
    ): List<String> {
        return personalisation.photoRejectionReasonsExcludingOther.map { reason ->
            photoRejectionReasonMapper.toPhotoRejectionReasonString(
                reason,
                languageDto,
            )
        }
    }

    protected fun mapSignatureRejectionReasons(
        languageDto: LanguageDto,
        rejectionReasons: List<SignatureRejectionReason>,
    ): List<String> {
        return rejectionReasons.map { reason ->
            signatureRejectionReasonMapper.toSignatureRejectionReasonString(
                reason,
                languageDto,
            )
        }
    }

    protected fun mapDocumentRejectionText(
        languageDto: LanguageDto,
        personalisation: IdDocumentPersonalisation,
        channel: CommunicationChannel,
    ): String? {
        return documentRejectionTextMapper.toDocumentRejectionText(
            language = languageDto,
            personalisation = personalisation,
            channel = channel,
        )
    }

    protected fun mapSignatureNotSuitableText(sourceType: String, includeText: Boolean, languageDto: LanguageDto): String? {
        return if (includeText) {
            signatureRejectionReasonMapper.toSignatureNotSuitableText(
                sourceType,
                languageDto,
            )
        } else {
            null
        }
    }
}
