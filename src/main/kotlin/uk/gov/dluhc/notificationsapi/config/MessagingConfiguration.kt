package uk.gov.dluhc.notificationsapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.messagingsupport.MessagingConfigurationHelper
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
    @Primary
    @Profile("!integration-test")
    fun sqsTemplate(
        sqsAsyncClient: SqsAsyncClient,
        sqsMessagingMessageConverter: SqsMessagingMessageConverter,
    ) = MessagingConfigurationHelper.sqsTemplate(sqsAsyncClient, sqsMessagingMessageConverter)

    @Bean
    fun triggerVoterCardStatisticsUpdateQueue(sqsTemplate: SqsTemplate) =
        MessageQueue<VoterCardUpdateStatisticsMessage>(triggerVoterCardStatisticsUpdateQueueName, sqsTemplate)

    @Bean
    fun triggerPostalApplicationStatisticsUpdateQueue(sqsTemplate: SqsTemplate) =
        MessageQueue<PostalUpdateStatisticsMessage>(triggerPostalApplicationStatisticsUpdateQueueName, sqsTemplate)

    @Bean
    fun triggerProxyApplicationStatisticsUpdateQueue(sqsTemplate: SqsTemplate) =
        MessageQueue<ProxyUpdateStatisticsMessage>(triggerProxyApplicationStatisticsUpdateQueueName, sqsTemplate)

    @Bean
    fun sqsMessagingMessageConverter(
        objectMapper: ObjectMapper,
    ) = MessagingConfigurationHelper.sqsMessagingMessageConverter(objectMapper)

    @Bean
    fun defaultSqsListenerContainerFactory(
        objectMapper: ObjectMapper,
        sqsAsyncClient: SqsAsyncClient,
        sqsMessagingMessageConverter: SqsMessagingMessageConverter,
    ) = MessagingConfigurationHelper.defaultSqsListenerContainerFactory(
        sqsAsyncClient,
        sqsMessagingMessageConverter,
        null,
    )
}
