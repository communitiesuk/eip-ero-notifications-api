package uk.gov.dluhc.notificationsapi.messaging

import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.messagingsupport.MessageListener
import uk.gov.dluhc.notificationsapi.mapper.SignatureReceivedPersonalisationMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifySignatureReceivedMessage
import uk.gov.dluhc.notificationsapi.service.SendNotificationService

private val logger = KotlinLogging.logger { }

@Component
class SendNotifySignatureReceivedMessageListener(
    private val sendNotifyMessageMapper: SendNotifyMessageMapper,
    private val sendNotificationService: SendNotificationService,
    private val signatureReceivedPersonalisationMapper: SignatureReceivedPersonalisationMapper,
) : MessageListener<SendNotifySignatureReceivedMessage> {

    @SqsListener(value = ["\${sqs.send-uk-gov-notify-signature-received-queue-name}"])
    override fun handleMessage(
        @Valid @Payload
        payload: SendNotifySignatureReceivedMessage,
    ) {
        with(payload) {
            logger.info {
                "Received request to send UK GOV Notify Signature Resubmission comms for gssCode: [$gssCode] with " +
                    "channel: [EMAIL], " +
                    "messageType: [$messageType], " +
                    "language: [$language]"
            }
            val sendNotificationRequestDto =
                sendNotifyMessageMapper.fromSignatureReceivedMessageToSendNotificationRequestDto(this)
            val personalisationMap =
                signatureReceivedPersonalisationMapper.fromMessagePersonalisationToBasePersonalisationMap(
                    personalisation,
                    sourceType,
                    language,
                )

            sendNotificationService.sendNotification(
                sendNotificationRequestDto,
                personalisationMap,
            )
        }
    }
}
