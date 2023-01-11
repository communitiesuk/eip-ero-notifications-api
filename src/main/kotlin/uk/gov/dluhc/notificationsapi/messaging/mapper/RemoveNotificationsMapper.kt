package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.RemoveNotificationsDto
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.RemoveApplicationNotificationsMessage

@Mapper(uses = [SourceTypeMapper::class])
interface RemoveNotificationsMapper {

    fun toRemoveNotificationsDto(message: RemoveApplicationNotificationsMessage): RemoveNotificationsDto
}
