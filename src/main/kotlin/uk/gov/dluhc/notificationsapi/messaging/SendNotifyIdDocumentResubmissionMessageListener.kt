package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.messaging.listener.annotation.SqsListener
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import javax.validation.Valid

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyIdDocumentResubmissionMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyIdDocumentResubmissionMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-id-document-resubmission-queue-name}"])
    override fun handleMessage(@Valid @Payload payload: SendNotifyIdDocumentResubmissionMessage) {
        logger.info {
            "received 'send UK Gov notify ID document message' request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromIdDocumentMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper.toIdDocumentPersonalisationDto(
                personalisationMessage = personalisation,
                languageDto = sendNotificationRequestDto.language,
                channel = sendNotificationRequestDto.channel
            )
            val personalisationMap = templatePersonalisationDtoMapper.toIdDocumentResubmissionTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
