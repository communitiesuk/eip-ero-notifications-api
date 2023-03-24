package uk.gov.dluhc.notificationsapi.config

import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.model.PurgeQueueRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.messaging.core.QueueMessagingTemplate
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.repository.CommunicationConfirmationRepository
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.testsupport.WiremockService
import uk.gov.dluhc.notificationsapi.testsupport.getDifferentRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.getRandomEroId
import uk.gov.service.notify.NotificationClient

/**
 * Base class used to bring up the entire Spring ApplicationContext
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [IntegrationTest.IntegrationTestConfiguration::class],
)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "PT5M")
internal abstract class IntegrationTest {

    @Autowired
    protected lateinit var govNotifyApiClient: GovNotifyApiClient

    @Autowired
    protected lateinit var notificationRepository: NotificationRepository

    @Autowired
    protected lateinit var communicationConfirmationRepository: CommunicationConfirmationRepository

    @Autowired
    protected lateinit var amazonSQSAsync: AmazonSQSAsync

    @Autowired
    protected lateinit var localStackContainerSettings: LocalStackContainerSettings

    @Autowired
    protected lateinit var sqsMessagingTemplate: QueueMessagingTemplate

    @Value("\${sqs.send-uk-gov-notify-photo-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyPhotoResubmissionQueueName: String

    @Value("\${sqs.send-uk-gov-notify-id-document-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyIdDocumentResubmissionQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-received-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationReceivedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-approved-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationApprovedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-rejected-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationRejectedQueueName: String

    @Value("\${sqs.remove-application-notifications-queue-name}")
    protected lateinit var removeApplicationNotificationsQueueName: String

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var wireMockService: WiremockService

    @Autowired
    protected lateinit var dynamoDbClient: DynamoDbClient

    @Autowired
    protected lateinit var dynamoDbConfiguration: DynamoDbConfiguration

    @Value("\${api.notify.template.voter-card.letter.rejected-english}")
    protected lateinit var applicationRejectedLetterEnglishTemplateId: String

    @Value("\${api.notify.template.voter-card.letter.rejected-welsh}")
    protected lateinit var applicationRejectedLetterWelshTemplateId: String

    @Value("\${api.notify.template.voter-card.email.id-document-required-english}")
    protected lateinit var idDocumentRequiredEmailEnglishTemplateId: String

    @Value("\${api.notify.template.voter-card.email.id-document-required-welsh}")
    protected lateinit var idDocumentRequiredEmailWelshTemplateId: String

    @Value("\${api.notify.template.voter-card.letter.id-document-required-english}")
    protected lateinit var idDocumentRequiredLetterEnglishTemplateId: String

    @Value("\${api.notify.template.voter-card.letter.id-document-required-welsh}")
    protected lateinit var idDocumentRequiredLetterWelshTemplateId: String

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        val ERO_ID = getRandomEroId()
        val OTHER_ERO_ID = getDifferentRandomEroId(ERO_ID)
        val localStackContainer = LocalStackContainerConfiguration.getInstance()
    }

    @BeforeEach
    fun resetWireMock() {
        wireMockService.resetAllStubsAndMappings()
    }

    @BeforeEach
    fun clearDatabase() {
        clearTable(dynamoDbConfiguration.notificationsTableName)
        clearTable(dynamoDbConfiguration.communicationConfirmationsTableName)
    }

    protected fun clearTable(tableName: String, partitionKey: String = "id", sortKey: String? = null) {
        val response = dynamoDbClient.scan(ScanRequest.builder().tableName(tableName).build())
        response.items().forEach {
            val keys = mutableMapOf<String, AttributeValue>(
                partitionKey to AttributeValue.builder().s(it[partitionKey]!!.s()).build(),
            )

            if (sortKey != null) {
                keys[sortKey] = AttributeValue.builder().s(it[partitionKey]!!.s()).build()
            }

            val deleteRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(keys)
                .build()

            dynamoDbClient.deleteItem(deleteRequest)
        }
    }

    @TestConfiguration
    class IntegrationTestConfiguration {
        @Bean
        @Primary
        fun testNotificationClient(@Value("\${api.notify.api-key}") apiKey: String, wiremockService: WiremockService) =
            NotificationClient(apiKey, wiremockService.wiremockBaseUrl())
    }

    @BeforeEach
    fun clearSqsQueues() {
        with(localStackContainerSettings) {
            clearSqsQueue(mappedQueueUrlSendUkGovNotifyPhotoResubmissionQueueName)
            clearSqsQueue(mappedQueueUrlSendUkGovNotifyIdDocumentResubmissionQueueName)
            clearSqsQueue(mappedQueueUrlSendUkGovNotifyApplicationReceivedQueueName)
            clearSqsQueue(mappedQueueUrlSendUkGovNotifyApplicationApprovedQueueName)
            clearSqsQueue(mappedQueueUrlSendUkGovNotifyApplicationRejectedMessageQueueName)
            clearSqsQueue(mappedQueueUrlRemoveApplicationNotificationsQueueName)
        }
    }

    private fun clearSqsQueue(queueUrl: String) {
        amazonSQSAsync.purgeQueue(PurgeQueueRequest(queueUrl))
    }
}
