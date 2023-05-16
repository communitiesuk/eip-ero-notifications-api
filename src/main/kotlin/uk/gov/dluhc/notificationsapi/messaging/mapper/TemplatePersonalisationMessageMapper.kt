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
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.mapper.PhotoRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation

@Mapper
abstract class TemplatePersonalisationMessageMapper {

    @Autowired
    protected lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @Autowired
    protected lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Mapping(
        target = "photoRejectionReasons",
        expression = "java( mapPhotoRejectionReasons( languageDto, personalisationMessage ) )"
    )
    abstract fun toPhotoPersonalisationDto(
        personalisationMessage: PhotoPersonalisation,
        languageDto: LanguageDto
    ): PhotoPersonalisationDto

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

    protected fun mapPhotoRejectionReasons(
        languageDto: LanguageDto,
        personalisation: PhotoPersonalisation
    ): List<String> {
        return personalisation.photoRejectionReasons.map { reason ->
            photoRejectionReasonMapper.toPhotoRejectionReasonString(
                reason,
                languageDto
            )
        }
    }
}
