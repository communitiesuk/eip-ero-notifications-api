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
import uk.gov.dluhc.applicationsapi.messaging.models.UpdateApplicationStatisticsMessage
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.messagingsupport.MessagingConfigurationHelper

@Configuration
class MessagingConfiguration {

    @Value("\${sqs.trigger-application-statistics-update-queue-name}")
    private lateinit var triggerApplicationStatisticsUpdateQueueName: String

    @Bean
    @Primary
    @Profile("!integration-test")
    fun sqsTemplate(
        sqsAsyncClient: SqsAsyncClient,
        sqsMessagingMessageConverter: SqsMessagingMessageConverter,
    ) = MessagingConfigurationHelper.sqsTemplate(sqsAsyncClient, sqsMessagingMessageConverter)

    @Bean
    fun triggerApplicationStatisticsUpdateQueue(sqsTemplate: SqsTemplate) =
        MessageQueue<UpdateApplicationStatisticsMessage>(triggerApplicationStatisticsUpdateQueueName, sqsTemplate)

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
