package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.messaging.models.PhotoResubmissionPersonalisation

@Mapper
interface PhotoResubmissionPersonalisationMessageMapper {

    fun toPhotoResubmissionPersonalisationDto(personalisationMessage: PhotoResubmissionPersonalisation): PhotoResubmissionPersonalisationDto
}
