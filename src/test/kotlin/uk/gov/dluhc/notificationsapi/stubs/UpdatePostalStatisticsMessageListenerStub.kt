package uk.gov.dluhc.notificationsapi.stubs

import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.postalapplicationsapi.messaging.models.UpdateStatisticsMessage

@Component
class UpdatePostalStatisticsMessageListenerStub : MessageListenerStub<UpdateStatisticsMessage>() {

    @SqsListener("\${sqs.trigger-postal-application-statistics-update-queue-name}")
    override fun handleMessage(
        @Valid @Payload
        payload: UpdateStatisticsMessage,
    ) {
        super.handleMessage(payload)
    }
}
