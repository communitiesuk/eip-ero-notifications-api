package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyInviteToRegisterMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyInviteToRegisterMessageListener(
        private val sendNotificationService: SendNotificationService,
        private val sendNotifyMessageMapper: SendNotifyMessageMapper,
        private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
        private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyInviteToRegisterMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-invite-to-register-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifyInviteToRegisterMessage
    ) {
        logger.info {
            "received send UK Gov notify invite to register message request for gssCode: ${payload.gssCode} with " +
                    "channel: ${payload.channel}, " +
                    "messageType: ${payload.messageType}, " +
                    "language: ${payload.language}, " +
                    "sourceReference: ${payload.sourceReference}, " +
                    "sourceType: ${payload.sourceType}"
        }
        with(payload) {
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromInviteToRegisterMessageToSendNotificationRequestDto(
                    this,
                )
            val personalisationDto = templatePersonalisationMessageMapper
                    .toInviteToRegisterTemplatePersonalisationDto(
                        personalisation,
                        sendNotificationRequestDto.language,
                        sourceType,
                    )
            val personalisationMap = templatePersonalisationDtoMapper.toInviteToRegisterTemplatePersonalisationMap(
                    personalisationDto,
                    sendNotificationRequestDto.language
            )
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
