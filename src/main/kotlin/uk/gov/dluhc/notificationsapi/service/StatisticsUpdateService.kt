package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.exception.InvalidSourceTypeException
import java.util.UUID
import uk.gov.dluhc.applicationsapi.messaging.models.UpdateStatisticsMessage as ApplicationUpdateStatisticsMessage
import uk.gov.dluhc.overseasapplicationsapi.messaging.models.UpdateStatisticsMessage as OverseasUpdateStatisticsMessage
import uk.gov.dluhc.postalapplicationsapi.messaging.models.UpdateStatisticsMessage as PostalUpdateStatisticsMessage
import uk.gov.dluhc.proxyapplicationsapi.messaging.models.UpdateStatisticsMessage as ProxyUpdateStatisticsMessage
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage as VoterCardUpdateStatisticsMessage

@Service
class StatisticsUpdateService(
    private val triggerVoterCardStatisticsUpdateQueue: MessageQueue<VoterCardUpdateStatisticsMessage>,
    private val triggerPostalApplicationStatisticsUpdateQueue: MessageQueue<PostalUpdateStatisticsMessage>,
    private val triggerProxyApplicationStatisticsUpdateQueue: MessageQueue<ProxyUpdateStatisticsMessage>,
    private val triggerOverseasApplicationStatisticsUpdateQueue: MessageQueue<OverseasUpdateStatisticsMessage>,
    private val triggerApplicationStatisticsUpdateQueue: MessageQueue<ApplicationUpdateStatisticsMessage>,
) {
    fun triggerStatisticsUpdate(applicationId: String, sourceType: SourceType, isFromApplicationApi: Boolean) {
        val deduplicationId = UUID.randomUUID().toString()

        if (isFromApplicationApi) {
            submitToTriggerApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
        } else {
            when (sourceType) {
                SourceType.VOTER_CARD -> submitToTriggerVoterCardStatisticsUpdateQueue(applicationId, deduplicationId)
                SourceType.POSTAL -> submitToTriggerPostalApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
                SourceType.PROXY -> submitToTriggerProxyApplicationStatisticsUpdateQueue(applicationId, deduplicationId)
                SourceType.OVERSEAS -> submitToTriggerOverseasStatisticsUpdateQueue(applicationId, deduplicationId)
                else -> {
                    throw InvalidSourceTypeException(sourceType.toString())
                }
            }
        }
    }

    fun submitToTriggerPostalApplicationStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = PostalUpdateStatisticsMessage(postalApplicationId = applicationId)
        triggerPostalApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun submitToTriggerProxyApplicationStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = ProxyUpdateStatisticsMessage(proxyApplicationId = applicationId)
        triggerProxyApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun submitToTriggerOverseasStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = OverseasUpdateStatisticsMessage(overseasApplicationId = applicationId)
        triggerOverseasApplicationStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
    }

    fun submitToTriggerVoterCardStatisticsUpdateQueue(applicationId: String, deduplicationId: String) {
        val updateMessage = VoterCardUpdateStatisticsMessage(voterCardApplicationId = applicationId)
        triggerVoterCardStatisticsUpdateQueue.submit(updateMessage, createMap(applicationId, deduplicationId))
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
