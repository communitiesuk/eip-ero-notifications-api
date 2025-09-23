package uk.gov.dluhc.notificationsapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
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
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import software.amazon.awssdk.services.sqs.model.PurgeQueueResponse
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.repository.CommunicationConfirmationRepository
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.stubs.UpdateApplicationStatisticsMessageListenerStub
import uk.gov.dluhc.notificationsapi.testsupport.WiremockService
import uk.gov.dluhc.notificationsapi.testsupport.getDifferentRandomEroId
import uk.gov.dluhc.notificationsapi.testsupport.getRandomEroId
import uk.gov.service.notify.NotificationClient
import java.time.Clock
import java.util.concurrent.CompletableFuture

/**
 * Base class used to bring up the entire Spring ApplicationContext
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [IntegrationTest.IntegrationTestConfiguration::class],
)
@ActiveProfiles("integration-test")
@AutoConfigureWebTestClient(timeout = "PT5M")
internal abstract class IntegrationTest {

    @Autowired
    protected lateinit var govNotifyApiClient: GovNotifyApiClient

    @Autowired
    protected lateinit var notificationRepository: NotificationRepository

    @Autowired
    protected lateinit var communicationConfirmationRepository: CommunicationConfirmationRepository

    @Autowired
    protected lateinit var amazonSQSAsync: SqsAsyncClient

    @Autowired
    protected lateinit var localStackContainerSettings: LocalStackContainerSettings

    @Autowired
    protected lateinit var sqsMessagingTemplate: SqsTemplate

    @Value("\${sqs.send-uk-gov-notify-photo-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyPhotoResubmissionQueueName: String

    @Value("\${sqs.send-uk-gov-notify-id-document-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyIdDocumentResubmissionQueueName: String

    @Value("\${sqs.send-uk-gov-notify-id-document-required-queue-name}")
    protected lateinit var sendUkGovNotifyIdDocumentRequiredQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-received-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationReceivedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-approved-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationApprovedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-application-rejected-queue-name}")
    protected lateinit var sendUkGovNotifyApplicationRejectedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-rejected-document-queue-name}")
    protected lateinit var sendUkGovNotifyRejectedDocumentQueueName: String

    @Value("\${sqs.send-uk-gov-notify-nino-not-matched-queue-name}")
    protected lateinit var sendUkGovNotifyNinoNotMatchedQueueName: String

    @Value("\${sqs.send-uk-gov-notify-bespoke-comm-queue-name}")
    protected lateinit var sendUkGovNotifyBespokeCommQueueName: String

    @Value("\${sqs.send-uk-gov-notify-not-registered-to-vote-queue-name}")
    protected lateinit var sendUkGovNotifyNotRegisteredToVoteQueueName: String

    @Value("\${sqs.remove-application-notifications-queue-name}")
    protected lateinit var removeApplicationNotificationsQueueName: String

    @Value("\${sqs.send-uk-gov-notify-rejected-signature-queue-name}")
    protected lateinit var sendUkGovNotifyRejectedSignatureQueueName: String

    @Value("\${sqs.send-uk-gov-notify-requested-signature-queue-name}")
    protected lateinit var sendUkGovNotifyRequestedSignatureQueueName: String

    @Value("\${sqs.send-uk-gov-notify-signature-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifySignatureResubmissionQueueName: String

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
    protected lateinit var updateApplicationStatisticsMessageListenerStub: UpdateApplicationStatisticsMessageListenerStub

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var clock: Clock

    companion object {
        val ERO_ID = getRandomEroId()
        val OTHER_ERO_ID = getDifferentRandomEroId(ERO_ID)

        @JvmStatic
        @BeforeAll
        fun setup() {
            LocalStackContainerConfiguration.getInstance()
        }
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

    @BeforeEach
    fun clearMessagesFromStubs() {
        updateApplicationStatisticsMessageListenerStub.clear()
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

    protected fun assertUpdateApplicationStatisticsMessageSent(applicationId: String) {
        val messages = updateApplicationStatisticsMessageListenerStub.getMessages()
        Assertions.assertThat(messages).isNotEmpty
        Assertions.assertThat(messages).anyMatch {
            it.externalId == applicationId
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
            CompletableFuture.allOf(
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyPhotoResubmissionQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyIdDocumentResubmissionQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyIdDocumentRequiredQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyApplicationReceivedQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyApplicationApprovedQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyApplicationRejectedMessageQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyRejectedDocumentMessageQueueName),
                clearSqsQueueAsync(mappedQueueUrlRemoveApplicationNotificationsQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyNinoNotMatchedMessageQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyBespokeCommMessageQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyNotRegisteredToVoteMessageQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyRequestedSignatureQueueName),
                clearSqsQueueAsync(mappedQueueUrlSendUkGovNotifyRejectedSignatureQueueName),
                clearSqsQueueAsync(sendUkGovNotifyRequestedSignatureQueueName),
                clearSqsQueueAsync(sendUkGovNotifyRejectedSignatureQueueName),
            ).join()
        }
    }

    fun clearSqsQueueAsync(queueUrl: String): CompletableFuture<PurgeQueueResponse> =
        amazonSQSAsync.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build())
}
