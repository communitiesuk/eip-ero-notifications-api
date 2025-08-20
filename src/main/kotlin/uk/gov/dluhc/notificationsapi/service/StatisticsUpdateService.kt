package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.applicationsapi.messaging.models.UpdateApplicationStatisticsMessage
import uk.gov.dluhc.messagingsupport.MessageQueue
import java.util.UUID

@Service
class StatisticsUpdateService(
    private val triggerApplicationStatisticsUpdateQueue: MessageQueue<UpdateApplicationStatisticsMessage>,
) {
    fun triggerStatisticsUpdate(applicationId: String) {
        val deduplicationId = UUID.randomUUID().toString()
        submitToTriggerApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
    }

    fun submitToTriggerApplicationStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = UpdateApplicationStatisticsMessage(externalId = applicationId)
        triggerApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun createMap(applicationId: String, deduplicationId: String): Map<String, Any> {
        return mapOf(
            "message-group-id" to applicationId,
            "message-deduplication-id" to deduplicationId,
        )
    }
}
