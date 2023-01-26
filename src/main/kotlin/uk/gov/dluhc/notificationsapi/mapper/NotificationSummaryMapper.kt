package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import java.time.OffsetDateTime
import java.time.ZoneOffset
import uk.gov.dluhc.notificationsapi.database.entity.NotificationSummary as NotificationSummaryEntity
import uk.gov.dluhc.notificationsapi.models.CommunicationsSummary as CommunicationsSummaryApi

@Mapper(
    uses = [NotificationTypeMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class],
    imports = [OffsetDateTime::class, ZoneOffset::class]
)
interface NotificationSummaryMapper {

    fun toNotificationSummaryDto(notificationSummary: NotificationSummaryEntity): NotificationSummaryDto

    @Mapping(target = "timestamp", expression = "java(OffsetDateTime.of(notificationSummaryDto.getSentAt(), ZoneOffset.UTC))")
    @Mapping(target = "templateType", source = "type")
    fun toCommunicationsSummaryApi(notificationSummaryDto: NotificationSummaryDto): CommunicationsSummaryApi
}
