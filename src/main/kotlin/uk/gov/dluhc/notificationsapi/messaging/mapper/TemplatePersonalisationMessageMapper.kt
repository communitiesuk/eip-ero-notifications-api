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
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.RejectedDocumentMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.messaging.models.RejectedDocumentPersonalisation

@Mapper
abstract class TemplatePersonalisationMessageMapper {
    @Autowired
    private lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Autowired
    private lateinit var rejectedDocumentMapper: RejectedDocumentMapper

    abstract fun toPhotoPersonalisationDto(personalisationMessage: PhotoPersonalisation): PhotoPersonalisationDto

    abstract fun toIdDocumentPersonalisationDto(personalisationMessage: IdDocumentPersonalisation): IdDocumentPersonalisationDto

    abstract fun toIdDocumentRequiredPersonalisationDto(personalisationMessage: IdDocumentRequiredPersonalisation): IdDocumentRequiredPersonalisationDto

    abstract fun toReceivedPersonalisationDto(personalisationMessage: BasePersonalisation): ApplicationReceivedPersonalisationDto

    abstract fun toApprovedPersonalisationDto(personalisationMessage: BasePersonalisation): ApplicationApprovedPersonalisationDto

    @Mapping(
        target = "rejectionReasonList",
        expression = "java( mapApplicationRejectionReasons( languageDto, personalisationMessage ) )"
    )
    abstract fun toRejectedPersonalisationDto(
        personalisationMessage: ApplicationRejectedPersonalisation,
        languageDto: LanguageDto
    ): ApplicationRejectedPersonalisationDto

    fun mapApplicationRejectionReasons(
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
        target = "documents",
        expression = "java( mapRejectedDocuments( languageDto, personalisationMessage.getDocuments() ) )"
    )
    @Mapping(target = "rejectedDocumentFreeText", source = "personalisationMessage.rejectedDocumentMessage")
    abstract fun toRejectedDocumentPersonalisationDto(personalisationMessage: RejectedDocumentPersonalisation, languageDto: LanguageDto): RejectedDocumentPersonalisationDto

    fun mapRejectedDocuments(languageDto: LanguageDto, documents: List<RejectedDocument>) =
        documents.map { document ->
            rejectedDocumentMapper.fromMessagingRejectedDocumentToString(languageDto, document)
        }
}
