package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.DocumentCategoryMapper
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
    private val documentCategoryMapper: DocumentCategoryMapper
) : MessageListener<SendNotifyNinoNotMatchedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-nino-not-matched-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyNinoNotMatchedMessage) {
        logger.info {
            "received send UK Gov notify required document message request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}, " +
                "sourceReference: ${payload.sourceReference}"
        }
        with(payload) {
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromRequiredDocumentMessageToSendNotificationRequestDto(
                    this,
                    documentCategoryMapper
                )
            val personalisationDto = templatePersonalisationMessageMapper
                .toRequiredDocumentTemplatePersonalisationDto(
                    personalisation,
                    sendNotificationRequestDto.language,
                    sourceType
                )
            val personalisationMap =
                templatePersonalisationDtoMapper.toRequiredDocumentTemplatePersonalisationMap(
                    personalisationDto,
                    sendNotificationRequestDto.sourceType
                )
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
