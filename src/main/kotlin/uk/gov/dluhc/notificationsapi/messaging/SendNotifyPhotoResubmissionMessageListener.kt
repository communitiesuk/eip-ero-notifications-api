package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionPersonalisationMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyPhotoResubmissionMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val photoResubmissionPersonalisationMapper: PhotoResubmissionPersonalisationMapper,
) : MessageListener<SendNotifyPhotoResubmissionMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-message-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyPhotoResubmissionMessage) {
        logger.info {
            "received 'send UK Gov notify message' request for gssCode: ${payload.gssCode} with" +
                "Channel: ${payload.channel}, " +
                "MessageType: ${payload.messageType}, " +
                "Language: ${payload.language}"
        }
        val sendNotificationRequestDto = sendNotifyMessageMapper.toSendNotificationRequestDto(payload)
        val personalisationMap =
            photoResubmissionPersonalisationMapper.toTemplatePersonalisationMap(
                sendNotifyMessageMapper.toPhotoResubmissionPersonalisationDto(payload.personalisation)
            )
        sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
    }
}
