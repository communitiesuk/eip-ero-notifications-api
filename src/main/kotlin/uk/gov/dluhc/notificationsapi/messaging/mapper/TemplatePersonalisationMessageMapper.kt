package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.messaging.models.BaseResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation

@Mapper
interface TemplatePersonalisationMessageMapper {

    fun toPhotoPersonalisationDto(personalisationMessage: PhotoPersonalisation): PhotoPersonalisationDto

    fun toIdDocumentPersonalisationDto(personalisationMessage: IdDocumentPersonalisation): IdDocumentPersonalisationDto

    fun toApprovedPersonalisationDto(personalisationMessage: BaseResubmissionPersonalisation): ApplicationApprovedPersonalisationDto
}
