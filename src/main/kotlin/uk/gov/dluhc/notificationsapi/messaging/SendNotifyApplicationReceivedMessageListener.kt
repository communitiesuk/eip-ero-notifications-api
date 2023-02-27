package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyApplicationReceivedMessageListener(
    // private val sendNotificationService: SendNotificationService,
    // private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    // private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    // private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyApplicationReceivedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-application-received-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyApplicationReceivedMessage) {
        logger.info {
            "received 'send UK Gov notify application received message' request for gssCode: ${payload.gssCode} with " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}"
        }
    }
}
