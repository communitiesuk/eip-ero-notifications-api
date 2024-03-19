package uk.gov.dluhc.notificationsapi.config

import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory
import io.awspring.cloud.messaging.core.QueueMessagingTemplate
import io.awspring.cloud.messaging.listener.support.AcknowledgmentHandlerMethodArgumentResolver
import io.awspring.cloud.messaging.listener.support.VisibilityHandlerMethodArgumentResolver
import io.awspring.cloud.messaging.support.NotificationSubjectArgumentResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver
import org.springframework.validation.Validator
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.postalapplicationsapi.messaging.models.UpdateStatisticsMessage as PostalUpdateStatisticsMessage
import uk.gov.dluhc.proxyapplicationsapi.messaging.models.UpdateStatisticsMessage as ProxyUpdateStatisticsMessage
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage as VoterCardUpdateStatisticsMessage

@Configuration
class MessagingConfiguration {

    @Value("\${sqs.trigger-voter-card-statistics-update-queue-name}")
    private lateinit var triggerVoterCardStatisticsUpdateQueueName: String

    @Value("\${sqs.trigger-postal-application-statistics-update-queue-name}")
    private lateinit var triggerPostalApplicationStatisticsUpdateQueueName: String

    @Value("\${sqs.trigger-proxy-application-statistics-update-queue-name}")
    private lateinit var triggerProxyApplicationStatisticsUpdateQueueName: String

    @Bean
    fun triggerVoterCardStatisticsUpdateQueue(queueMessagingTemplate: QueueMessagingTemplate) =
        MessageQueue<VoterCardUpdateStatisticsMessage>(triggerVoterCardStatisticsUpdateQueueName, queueMessagingTemplate)

    @Bean
    fun triggerPostalApplicationStatisticsUpdateQueue(queueMessagingTemplate: QueueMessagingTemplate) =
        MessageQueue<PostalUpdateStatisticsMessage>(triggerPostalApplicationStatisticsUpdateQueueName, queueMessagingTemplate)

    @Bean
    fun triggerProxyApplicationStatisticsUpdateQueue(queueMessagingTemplate: QueueMessagingTemplate) =
        MessageQueue<ProxyUpdateStatisticsMessage>(triggerProxyApplicationStatisticsUpdateQueueName, queueMessagingTemplate)

    @Bean
    fun queueMessageHandlerFactory(
        jacksonMessageConverter: MappingJackson2MessageConverter,
        hibernateValidator: Validator
    ): QueueMessageHandlerFactory =
        QueueMessageHandlerFactory().apply {
            setArgumentResolvers(
                listOf(
                    HeadersMethodArgumentResolver(),
                    NotificationSubjectArgumentResolver(),
                    AcknowledgmentHandlerMethodArgumentResolver("Acknowledgment"),
                    VisibilityHandlerMethodArgumentResolver("Visibility"),
                    PayloadMethodArgumentResolver(jacksonMessageConverter, hibernateValidator)
                )
            )
        }
}
