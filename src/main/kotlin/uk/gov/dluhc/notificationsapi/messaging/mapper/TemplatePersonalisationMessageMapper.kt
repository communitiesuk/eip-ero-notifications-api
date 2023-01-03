package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoPersonalisation

@Mapper
interface TemplatePersonalisationMessageMapper {

    fun toPhotoPersonalisationDto(personalisationMessage: PhotoPersonalisation): PhotoPersonalisationDto
}
