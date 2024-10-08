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
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService

private val logger = KotlinLogging.logger { }

@Component
class SendNotifyPhotoResubmissionMessageListener(
    private val sendNotificationService: SendNotificationService,
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper,
    private val templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper,
) : MessageListener<SendNotifyPhotoResubmissionMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-photo-resubmission-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifyPhotoResubmissionMessage,
    ) {
        logger.info {
            "received 'send UK Gov notify photo message' request for gssCode: ${payload.gssCode} with " +
                "channel: ${payload.channel}, " +
                "messageType: ${payload.messageType}, " +
                "language: ${payload.language}"
        }
        with(payload) {
            val sendNotificationRequestDto = sendNotifyMessageMapper.fromPhotoMessageToSendNotificationRequestDto(this)
            val personalisationDto = templatePersonalisationMessageMapper.toPhotoPersonalisationDto(personalisation, sendNotificationRequestDto.language)
            val personalisationMap = templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(personalisationDto)
            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
