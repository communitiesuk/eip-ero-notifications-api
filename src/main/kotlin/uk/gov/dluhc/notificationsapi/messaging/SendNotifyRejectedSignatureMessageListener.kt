package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRejectedSignatureMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyRejectedSignatureMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyRejectedSignatureMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-rejected-signature-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyRejectedSignatureMessage) {

        with(payload) {
            logger.info {
                "received send rejected signature message with gssCode: $gssCode," +
                    "channel: $channel, " +
                    "messageType: $messageType, " +
                    "language: $language"
            }
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromRejectedSignatureMessageToSendNotificationRequestDto(this)
            val personalisationDto =
                templatePersonalisationMessageMapper.toRejectedSignaturePersonalisationDto(personalisation)
            val personalisationMap =
                templatePersonalisationDtoMapper.toRejectedSignatureTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(
                sendNotificationRequestDto, personalisationMap
            )
        }
    }
}
