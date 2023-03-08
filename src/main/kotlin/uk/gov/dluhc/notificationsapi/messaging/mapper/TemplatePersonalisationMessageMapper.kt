package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.mapper.ApplicationRejectionReasonMapper
import uk.gov.dluhc.notificationsapi.messaging.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation

@Mapper(uses = [ApplicationRejectionReasonMapper::class])
interface TemplatePersonalisationMessageMapper {

    fun toPhotoPersonalisationDto(personalisationMessage: PhotoPersonalisation): PhotoPersonalisationDto

    fun toIdDocumentPersonalisationDto(personalisationMessage: IdDocumentPersonalisation): IdDocumentPersonalisationDto

    fun toReceivedPersonalisationDto(personalisationMessage: BasePersonalisation): ApplicationReceivedPersonalisationDto

    fun toApprovedPersonalisationDto(personalisationMessage: BasePersonalisation): ApplicationApprovedPersonalisationDto

    fun toRejectedPersonalisationDto(personalisationMessage: ApplicationRejectedPersonalisation): ApplicationRejectedPersonalisationDto
}
