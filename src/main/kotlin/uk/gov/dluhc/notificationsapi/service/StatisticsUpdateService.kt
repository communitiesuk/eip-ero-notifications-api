package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.notificationsapi.dto.SourceType
import java.util.UUID
import uk.gov.dluhc.postalapplicationsapi.messaging.models.UpdateStatisticsMessage as PostalUpdateStatisticsMessage
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage as VoterCardUpdateStatisticsMessage

@Service
class StatisticsUpdateService(
    private val triggerVoterCardStatisticsUpdateQueue: MessageQueue<VoterCardUpdateStatisticsMessage>,
    private val triggerPostalApplicationStatisticsUpdateQueue: MessageQueue<PostalUpdateStatisticsMessage>
) {
    fun triggerStatisticsUpdate(applicationId: String, sourceType: SourceType) {
        val deduplicationId = UUID.randomUUID().toString()

        when (sourceType) {
            SourceType.VOTER_CARD -> submitToTriggerVoterCardStatisticsUpdateQueue(applicationId, deduplicationId)
            SourceType.POSTAL -> submitToTriggerPostalApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
            // TODO: EIP1-8742 Add proxy update
            else -> {}
        }
    }

    fun submitToTriggerPostalApplicationStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = PostalUpdateStatisticsMessage(postalApplicationId = applicationId)
        triggerPostalApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun submitToTriggerVoterCardStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = VoterCardUpdateStatisticsMessage(voterCardApplicationId = applicationId)
        triggerVoterCardStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun createMap(applicationId: String, deduplicationId: String): Map<String, Any> {
        return mapOf(
            "message-group-id" to applicationId,
            "message-deduplication-id" to deduplicationId
        )
    }
}
