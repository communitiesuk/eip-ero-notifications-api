package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyNinoNotMatchedMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyNinoNotMatchedMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyNinoNotMatchedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-nino-not-matched-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyNinoNotMatchedMessage) {
        logger.info {
            "received 'send UK Gov notify NiNo not matched message' request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}, " +
                "sourceReference: ${payload.sourceReference}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromNinoNotMatchedMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper
                .toNinoNotMatchedPersonalisationDto(personalisation, sendNotificationRequestDto.language, sourceType)
            val personalisationMap = templatePersonalisationDtoMapper.toNinoNotMatchedTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
