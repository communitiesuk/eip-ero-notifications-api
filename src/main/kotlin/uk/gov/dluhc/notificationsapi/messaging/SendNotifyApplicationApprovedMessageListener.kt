package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyApplicationApprovedMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyApplicationApprovedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-application-approved-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyApplicationApprovedMessage) {
        logger.info {
            "received 'send UK Gov notify application approved message' request for gssCode: ${payload.gssCode} with " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromApprovedMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper.toApprovedPersonalisationDto(personalisation)
            val personalisationMap = templatePersonalisationDtoMapper.toApplicationApprovedTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
