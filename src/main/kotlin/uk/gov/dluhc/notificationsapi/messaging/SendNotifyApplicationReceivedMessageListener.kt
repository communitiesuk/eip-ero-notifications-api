package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationReceivedMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyApplicationReceivedMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyApplicationReceivedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-application-received-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifyApplicationReceivedMessage,
    ) {
        logger.info {
            "received 'send UK Gov notify application received message' request for gssCode: ${payload.gssCode} with " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}, " +
                "sourceType: ${payload.sourceType}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromReceivedMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper
                .toReceivedPersonalisationDto(personalisation, sendNotificationRequestDto.language, sourceType)
            val personalisationMap = templatePersonalisationDtoMapper.toApplicationReceivedTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
