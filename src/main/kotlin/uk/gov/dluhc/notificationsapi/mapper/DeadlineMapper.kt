package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class DeadlineMapper(private val messageSource: MessageSource) {

    fun toDeadlineString(
        deadlineDate: LocalDate,
        deadlineTime: String?,
        languageDto: LanguageDto,
        sourceTypeString: String,
    ): String {
        val formattedDeadlineDate: String = deadlineDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", languageDto.locale))
        if (deadlineTime != null) {
            return messageSource.getMessage(
                "templates.deadline.deadlineWithTime",
                arrayOf(deadlineTime, formattedDeadlineDate, sourceTypeString),
                languageDto.locale,
            )
        }

        return messageSource.getMessage(
            "templates.deadline.deadlineWithoutTime",
            arrayOf(formattedDeadlineDate, sourceTypeString),
            languageDto.locale,
        )
    }
}
