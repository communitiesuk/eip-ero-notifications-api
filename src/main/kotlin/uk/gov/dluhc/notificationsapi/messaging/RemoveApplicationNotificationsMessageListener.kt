package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.messaging.mapper.RemoveNotificationsMapper
import uk.gov.dluhc.notificationsapi.messaging.models.RemoveApplicationNotificationsMessage
import uk.gov.dluhc.notificationsapi.service.RemoveNotificationsService
import javax.validation.Valid

private val logger = KotlinLogging.logger {}

@Component
class RemoveApplicationNotificationsMessageListener(
    private val removeNotificationsService: RemoveNotificationsService,
    private val removeNotificationsMapper: RemoveNotificationsMapper
) : MessageListener<RemoveApplicationNotificationsMessage> {

    @SqsListener(value = ["\${sqs.remove-application-notifications-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: RemoveApplicationNotificationsMessage) {
        with(payload) {
            logger.info {
                "RemoveApplicationNotificationsMessage received with " +
                    "sourceType: $sourceType and " +
                    "sourceReference: $sourceReference"
            }
            removeNotificationsService.remove(removeNotificationsMapper.toRemoveNotificationsDto(this))
        }
    }
}
