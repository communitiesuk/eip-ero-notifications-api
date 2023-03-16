package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Component
class DateTimeMapper {
    fun toUtcOffset(localDateTime: LocalDateTime?) = localDateTime?.atOffset(UTC)
}
