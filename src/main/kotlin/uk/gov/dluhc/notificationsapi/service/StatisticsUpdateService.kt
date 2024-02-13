package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage
import java.util.UUID

@Service
class StatisticsUpdateService(
    private val triggerVoterCardStatisticsUpdateQueue: MessageQueue<UpdateStatisticsMessage>,
    private val triggerPostalApplicationStatisticsUpdateQueue: MessageQueue<UpdateStatisticsMessage>
) {
    fun triggerStatisticsUpdate(applicationId: String, sourceType: SourceType) {
        val deduplicationId = UUID.randomUUID().toString()
        val updateMessage = UpdateStatisticsMessage(applicationId = applicationId)

        when (sourceType) {
            SourceType.VOTER_CARD -> triggerVoterCardStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
            SourceType.POSTAL -> triggerPostalApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
            // TODO: EIP1-8742 Add proxy update
            else -> {}
        }
    }

    fun createMap(applicationId: String, deduplicationId: String): Map<String, Any> {
        return mapOf(
            "message-group-id" to applicationId,
            "message-deduplication-id" to deduplicationId
        )
    }
}
