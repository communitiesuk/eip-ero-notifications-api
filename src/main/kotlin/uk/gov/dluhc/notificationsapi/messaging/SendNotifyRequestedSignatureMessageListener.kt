package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyRequestedSignatureMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyRequestedSignatureMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyRequestedSignatureMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-requested-signature-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifyRequestedSignatureMessage,
    ) {
        with(payload) {
            logger.info {
                "received send requested signature message with gssCode: $gssCode," +
                    "channel: $channel, " +
                    "messageType: $messageType, " +
                    "language: $language"
            }
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromRequestedSignatureToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper
                .toRequestedSignaturePersonalisationDto(personalisation, sendNotificationRequestDto.language, sourceType)
            val personalisationMap =
                templatePersonalisationDtoMapper.toRequestedSignatureTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(
                sendNotificationRequestDto,
                personalisationMap,
            )
        }
    }
}
