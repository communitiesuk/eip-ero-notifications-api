package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.messagingsupport.MessageQueue
import java.util.UUID
import uk.gov.dluhc.applicationsapi.messaging.models.UpdateStatisticsMessage as ApplicationUpdateStatisticsMessage

@Service
class StatisticsUpdateService(
    private val triggerApplicationStatisticsUpdateQueue: MessageQueue<ApplicationUpdateStatisticsMessage>,
) {
    fun triggerStatisticsUpdate(applicationId: String) {
        val deduplicationId = UUID.randomUUID().toString()
        submitToTriggerApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
    }

    fun submitToTriggerApplicationStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = ApplicationUpdateStatisticsMessage(applicationId = applicationId)
        triggerApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun createMap(applicationId: String, deduplicationId: String): Map<String, Any> {
        return mapOf(
            "message-group-id" to applicationId,
            "message-deduplication-id" to deduplicationId,
        )
    }
}
