package uk.gov.dluhc.notificationsapi.config

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
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.testsupport.WiremockService
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
    protected lateinit var sqsMessagingTemplate: QueueMessagingTemplate

    @Value("\${sqs.send-uk-gov-notify-photo-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyPhotoResubmissionQueueName: String

    @Value("\${sqs.send-uk-gov-notify-id-document-resubmission-queue-name}")
    protected lateinit var sendUkGovNotifyIdDocumentResubmissionQueueName: String

    @Value("\${sqs.remove-application-notifications-queue-name}")
    protected lateinit var removeApplicationNotificationsQueueName: String

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var wireMockService: WiremockService

    @Autowired
    protected lateinit var dynamoDbClient: DynamoDbClient

    @Autowired
    protected lateinit var tableConfig: DynamoDbConfiguration

    companion object {
        val localStackContainer = LocalStackContainerConfiguration.getInstance()
    }

    @BeforeEach
    fun resetWireMock() {
        wireMockService.resetAllStubsAndMappings()
    }

    fun deleteNotifications(notificationsToDelete: List<Notification>) {
        notificationsToDelete.forEach {
            dynamoDbClient.deleteItem(
                DeleteItemRequest.builder()
                    .tableName(tableConfig.notificationsTableName)
                    .key(
                        mapOf(
                            "id" to AttributeValue.fromS(it.id.toString()),
                        )
                    )
                    .build()
            )
        }
    }

    @TestConfiguration
    class IntegrationTestConfiguration {
        @Bean
        @Primary
        fun testNotificationClient(@Value("\${api.notify.api-key}") apiKey: String, wiremockService: WiremockService) =
            NotificationClient(apiKey, wiremockService.wiremockBaseUrl())
    }
}
