package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.*
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.mapper.*
import uk.gov.dluhc.notificationsapi.messaging.models.*
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

    @Mapping(
        target = "photoRejectionReasons",
        expression = "java( mapPhotoRejectionReasons( languageDto, personalisationMessage ) )"
    )
    abstract fun toPhotoPersonalisationDto(
        personalisationMessage: PhotoPersonalisation,
        languageDto: LanguageDto
    ): PhotoPersonalisationDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    @Mapping(
        target = "rejectionReasons",
        expression = "java( mapSignatureRejectionReasons( languageDto, personalisationMessage ) )"
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
        expression = "java( mapDocumentRejectionText( languageDto, personalisationMessage, channel ) )"
    )
    abstract fun toIdDocumentPersonalisationDto(
        personalisationMessage: IdDocumentPersonalisation,
        languageDto: LanguageDto,
        channel: NotificationChannel
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
        expression = "java( mapApplicationRejectionReasons( languageDto, personalisationMessage ) )"
    )
    abstract fun toRejectedPersonalisationDto(
        personalisationMessage: ApplicationRejectedPersonalisation,
        languageDto: LanguageDto,
    ): ApplicationRejectedPersonalisationDto

    protected fun mapApplicationRejectionReasons(
        languageDto: LanguageDto,
        personalisationMessage: ApplicationRejectedPersonalisation
    ): List<String> {
        return personalisationMessage.rejectionReasonList.map { reason ->
            applicationRejectionReasonMapper.toApplicationRejectionReasonString(
                reason,
                languageDto
            )
        }
    }

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( mapSourceType( languageDto, sourceType ) )",
    )
    @Mapping(
        target = "documents",
        expression = "java( rejectedDocumentsMapper.mapRejectionDocumentsFromMessaging(languageDto, personalisation.getDocuments()) )"
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
            target = "personalisationSourceTypeString",
            expression = "java( mapFullSourceType( languageDto, sourceType ) )",
    )
    @Mapping(target = "subjectHeader", source = "personalisation.subjectHeader")
    @Mapping(target = "details", source = "personalisation.details")
    @Mapping(target = "whatToDo", source = "personalisation.whatToDo")
    @Mapping(
            target = "deadline",
            expression = "java( mapDeadline( personalisation.getDeadlineDate(), personalisation.getDeadlineTime(), languageDto, sourceType ) )"
    )
    abstract fun toBespokeCommTemplatePersonalisationDto(
            personalisation: BespokeCommPersonalisation,
            languageDto: LanguageDto,
            sourceType: SourceType,
    ): BespokeCommunicationPersonalisationDto

    protected fun mapSourceType(
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): String = sourceTypeMapper.toSourceTypeString(sourceType, languageDto)

    protected fun mapFullSourceType(
            languageDto: LanguageDto,
            sourceType: SourceType
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
        personalisation: PhotoPersonalisation
    ): List<String> {
        return personalisation.photoRejectionReasonsExcludingOther.map { reason ->
            photoRejectionReasonMapper.toPhotoRejectionReasonString(
                reason,
                languageDto
            )
        }
    }

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

    protected fun mapDocumentRejectionText(
        languageDto: LanguageDto,
        personalisation: IdDocumentPersonalisation,
        channel: NotificationChannel
    ): String? {
        return documentRejectionTextMapper.toDocumentRejectionText(
            language = languageDto,
            personalisation = personalisation,
            channel = channel
        )
    }
}
