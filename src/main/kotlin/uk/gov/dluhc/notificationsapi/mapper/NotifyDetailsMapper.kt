package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.NotifyDetailsDto

@Mapper
interface NotifyDetailsMapper {

    fun fromEntityToDto(notificationSummary: NotifyDetails): NotifyDetailsDto
}
