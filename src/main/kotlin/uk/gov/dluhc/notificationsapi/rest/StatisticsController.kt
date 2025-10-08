package uk.gov.dluhc.notificationsapi.rest

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.InvalidSourceTypeException
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.service.StatisticsRetrievalService

@RestController
@CrossOrigin
@RequestMapping("/communications/statistics")
class StatisticsController(
    private val statisticsRetrievalService: StatisticsRetrievalService,
) {
    @GetMapping("/{service}/{applicationId}")
    fun getCommunicationsStatistics(
        @PathVariable service: String,
        @PathVariable applicationId: String,
    ): CommunicationsStatisticsResponse {
        val source = when (service) {
            "postal" -> SourceType.POSTAL
            "proxy" -> SourceType.PROXY
            "overseas" -> SourceType.OVERSEAS
            "voter-card" -> SourceType.VOTER_CARD
            else -> throw InvalidSourceTypeException(service)
        }

        return statisticsRetrievalService.getApplicationStatistics(source, applicationId)
    }
}
