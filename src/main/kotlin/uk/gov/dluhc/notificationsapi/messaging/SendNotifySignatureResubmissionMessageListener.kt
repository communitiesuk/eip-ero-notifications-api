package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.SignatureResubmissionPersonalisationMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifySignatureResubmissionMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService

private val logger = KotlinLogging.logger { }

@Component
class SendNotifySignatureResubmissionMessageListener(
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val sendNotificationService: SendNotificationService,
    private val signatureResubmissionPersonalisationMapper: SignatureResubmissionPersonalisationMapper,
) : MessageListener<SendNotifySignatureResubmissionMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-signature-resubmission-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifySignatureResubmissionMessage,
    ) {
        with(payload) {
            logger.info {
                "Received request to send UK GOV Notify Signature Resubmission comms for gssCode: $gssCode with " +
                    "channel: $channel, " +
                    "messageType: $messageType, " +
                    "language: $language"
            }
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromSignatureResubmissionMessageToSendNotificationRequestDto(this)
            val personalisationMap =
                signatureResubmissionPersonalisationMapper.fromMessagePersonalisationToPersonalisationMap(
                    personalisation,
                    sourceType,
                    language,
                )

            sendNotificationService.sendNotification(sendNotificationRequestDto, personalisationMap)
        }
    }
}
