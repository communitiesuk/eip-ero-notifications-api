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
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyBespokeCommMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyBespokeCommMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyBespokeCommMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-bespoke-comm-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifyBespokeCommMessage,
    ) {
        logger.info {
            "received send UK Gov notify bespoke communication message request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}, " +
                "sourceReference: ${payload.sourceReference}, " +
                "sourceType: ${payload.sourceType}"
        }
        with(payload) {
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromBespokeCommMessageToSendNotificationRequestDto(
                    this,
                )
            val personalisationDto = templatePersonalisationMessageMapper
                .toBespokeCommTemplatePersonalisationDto(
                    personalisation,
                    sendNotificationRequestDto.language,
                    sourceType,
                )
            val personalisationMap = templatePersonalisationDtoMapper.toBespokeCommTemplatePersonalisationMap(
                personalisationDto,
                sendNotificationRequestDto.language,
            )
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
