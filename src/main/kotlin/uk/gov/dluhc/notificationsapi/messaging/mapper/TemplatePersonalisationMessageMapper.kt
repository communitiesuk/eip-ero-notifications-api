package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.IdentityDocumentResubmissionDocumentRejectionTextMapper
import uk.gov.dluhc.notificationsapi.mapper.PhotoRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentsMapper
import uk.gov.dluhc.notificationsapi.mapper.SignatureRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.NinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RequestedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType

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

    protected fun mapSourceType(
        languageDto: LanguageDto,
        sourceType: SourceType,
    ): String = sourceTypeMapper.toSourceTypeString(sourceType, languageDto)

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
