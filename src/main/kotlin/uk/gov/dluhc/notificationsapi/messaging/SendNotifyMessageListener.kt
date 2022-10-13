package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS
import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiBadRequestException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiNotFoundException
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val notifySendMessageMapper: SendNotifyMessageMapper
) : MessageListener<SendNotifyMessage> {
    @SqsListener(value = ["\${sqs.send-uk-gov-notify-message-queue-name}"], deletionPolicy = ON_SUCCESS)
    override fun handleMessage(@Valid @Payload payload: SendNotifyMessage) {
        logger.info { "received 'send UK Gov notify message' request" }
        val request = notifySendMessageMapper.toSendNotificationRequestDto(payload)
        sendNotificationService.sendNotification(request)
    }

    @MessageExceptionHandler(value = [GovNotifyApiNotFoundException::class, GovNotifyApiBadRequestException::class])
    fun exceptionHandler() {
        logger.info("Notify service failed with non-retryable Gov Notify client error")
    }
}
