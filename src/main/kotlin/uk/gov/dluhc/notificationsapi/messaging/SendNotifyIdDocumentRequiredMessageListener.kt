package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentRequiredMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyIdDocumentRequiredMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyIdDocumentRequiredMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-id-document-required-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyIdDocumentRequiredMessage) {
        logger.info {
            "received 'send UK Gov notify new ID document required message' request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromIdDocumentRequiredMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper.toIdDocumentRequiredPersonalisationDto(personalisation)
            val personalisationMap = templatePersonalisationDtoMapper.toIdDocumentRequiredTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
