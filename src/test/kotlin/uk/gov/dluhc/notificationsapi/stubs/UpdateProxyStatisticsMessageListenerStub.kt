package uk.gov.dluhc.notificationsapi.stubs

import io.awspring.cloud.sqs.annotation.SqsListener
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import uk.gov.dluhc.proxyapplicationsapi.messaging.models.UpdateStatisticsMessage

@Component
class UpdateProxyStatisticsMessageListenerStub : MessageListenerStub<UpdateStatisticsMessage>() {

    @SqsListener("\${sqs.trigger-proxy-application-statistics-update-queue-name}")
    override fun handleMessage(
        @Valid @Payload
        payload: UpdateStatisticsMessage,
    ) {
        super.handleMessage(payload)
    }
}
