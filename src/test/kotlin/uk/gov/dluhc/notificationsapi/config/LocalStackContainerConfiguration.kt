package uk.gov.dluhc.notificationsapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
import uk.gov.dluhc.notificationsapi.database.entity.COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME
import uk.gov.dluhc.notificationsapi.database.entity.SOURCE_REFERENCE_INDEX_NAME
import java.net.InetAddress
import java.net.URI

/**
 * Configuration class exposing beans for the LocalStack (AWS) environment.
 */
@Configuration
class LocalStackContainerConfiguration {

    companion object {
        private const val DEFAULT_REGION = "eu-west-2"
        const val DEFAULT_PORT = 4566
        const val DEFAULT_ACCESS_KEY_ID = "test"
        const val DEFAULT_SECRET_KEY = "test"

        val objectMapper = ObjectMapper()
        val localStackContainer: GenericContainer<*> = getInstance()
        private var container: GenericContainer<*>? = null

        /**
         * Creates and starts LocalStack configured with a basic (empty) SQS service.
         * Returns the container that can subsequently be used for further setup and configuration.
         */
        fun getInstance(): GenericContainer<*> {
            if (container == null) {
                container = GenericContainer(
                    DockerImageName.parse("localstack/localstack:1.1.0")
                ).withEnv(
                    mapOf(
                        "SERVICES" to "dynamodb, sqs",
                        "AWS_DEFAULT_REGION" to DEFAULT_REGION,
                    )
                )
                    .withReuse(true)
                    .withExposedPorts(DEFAULT_PORT)
                    .withCreateContainerCmdModifier { it.withName("notifications-api-integration-test-localstack") }
                    .apply {
                        start()
                    }
            }

            return container!!
        }
    }

    @Bean
    fun awsBasicCredentialsProvider(): AwsCredentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(DEFAULT_ACCESS_KEY_ID, DEFAULT_SECRET_KEY))

    /**
     * Uses the localstack container to configure the various services.
     *
     * @return a [LocalStackContainerSettings] bean encapsulating the various IDs etc of the configured container and services.
     */
    @Bean
    fun localStackContainerSqsSettings(
        applicationContext: ConfigurableApplicationContext,
        @Value("\${sqs.send-uk-gov-notify-photo-resubmission-queue-name}") sendUkGovNotifyPhotoResubmissionQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-id-document-resubmission-queue-name}") sendUkGovNotifyIdDocumentResubmissionQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-id-document-required-queue-name}") sendUkGovNotifyIdDocumentRequiredQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-application-received-queue-name}") sendUkGovNotifyApplicationReceivedQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-application-approved-queue-name}") sendUkGovNotifyApplicationApprovedQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-application-rejected-queue-name}") sendUkGovNotifyApplicationRejectedQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-rejected-document-queue-name}") sendUkGovNotifyRejectedDocumentQueueName: String,
        @Value("\${sqs.remove-application-notifications-queue-name}") removeApplicationNotificationsQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-rejected-signature-queue-name}") sendUkGovNotifyRejectedSignatureQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-requested-signature-queue-name}") sendUkGovNotifyRequestedSignatureQueueName: String,
        @Value("\${sqs.send-uk-gov-notify-nino-not-matched-queue-name}") sendUkGovNotifyNinoNotMatchedQueueName: String,
        @Value("\${sqs.trigger-voter-card-statistics-update-queue-name}") triggerVoterCardStatisticsUpdateQueueName: String,
    ): LocalStackContainerSettings {
        val sendUkGovNotifyPhotoResubmissionMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyPhotoResubmissionQueueName)
        val sendUkGovNotifyIdDocumentResubmissionMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyIdDocumentResubmissionQueueName)
        val sendUkGovNotifyIdDocumentRequiredMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyIdDocumentRequiredQueueName)
        val sendUkGovNotifyApplicationReceivedMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyApplicationReceivedQueueName)
        val sendUkGovNotifyApplicationApprovedMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyApplicationApprovedQueueName)
        val sendUkGovNotifyApplicationRejectedMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyApplicationRejectedQueueName)
        val sendUkGovNotifyRejectedDocumentMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyRejectedDocumentQueueName)
        val sendUkGovNotifyNinoNotMatchedMessageQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyNinoNotMatchedQueueName)
        val removeApplicationNotificationsMessageQueueName = localStackContainer.createSqsQueue(removeApplicationNotificationsQueueName)
        val triggerVoterCardStatisticsMessageQueueName = localStackContainer.createSqsQueue(triggerVoterCardStatisticsUpdateQueueName)
        localStackContainer.createSqsQueue(sendUkGovNotifyRejectedSignatureQueueName)
        localStackContainer.createSqsQueue(sendUkGovNotifyRequestedSignatureQueueName)

        val apiUrl = "http://${localStackContainer.host}:${localStackContainer.getMappedPort(DEFAULT_PORT)}"

        TestPropertyValues.of("cloud.aws.sqs.endpoint=$apiUrl").applyTo(applicationContext)

        return LocalStackContainerSettings(
            apiUrl = apiUrl,
            sendUkGovNotifyPhotoResubmissionQueueName = sendUkGovNotifyPhotoResubmissionMessageQueueName,
            sendUkGovNotifyIdDocumentResubmissionQueueName = sendUkGovNotifyIdDocumentResubmissionMessageQueueName,
            sendUkGovNotifyIdDocumentRequiredMessageQueueName = sendUkGovNotifyIdDocumentRequiredMessageQueueName,
            sendUkGovNotifyApplicationReceivedQueueName = sendUkGovNotifyApplicationReceivedMessageQueueName,
            sendUkGovNotifyApplicationApprovedQueueName = sendUkGovNotifyApplicationApprovedMessageQueueName,
            sendUkGovNotifyApplicationRejectedMessageQueueName = sendUkGovNotifyApplicationRejectedMessageQueueName,
            sendUkGovNotifyRejectedDocumentMessageQueueName = sendUkGovNotifyRejectedDocumentMessageQueueName,
            removeApplicationNotificationsQueueName = removeApplicationNotificationsMessageQueueName,
            sendUkGovNotifyNinoNotMatchedMessageQueueName = sendUkGovNotifyNinoNotMatchedMessageQueueName,
            triggerVoterCardStatisticsUpdateQueueName = triggerVoterCardStatisticsMessageQueueName,
        )
    }

    private fun GenericContainer<*>.createSqsQueue(queueName: String): String {
        val isFifo = queueName.endsWith(".fifo")
        val attributes =
            if (isFifo) "VisibilityTimeout=1,MessageRetentionPeriod=5,FifoQueue=true"
            else "VisibilityTimeout=1,MessageRetentionPeriod=5"
        val execInContainer = execInContainer(
            "awslocal", "sqs", "create-queue", "--queue-name", queueName, "--attributes", attributes
        )
        return execInContainer.stdout.let {
            objectMapper.readValue(it, Map::class.java)
        }.let {
            it["QueueUrl"] as String
        }
    }

    @Primary
    @Bean
    fun testDynamoDbClient(
        testAwsCredentialsProvider: AwsCredentialsProvider,
        dbConfiguration: DynamoDbConfiguration
    ): DynamoDbClient {
        val dynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(testAwsCredentialsProvider)
            .endpointOverride(localStackContainer.getEndpointOverride())
            .build()

        createNotificationsTable(dynamoDbClient, dbConfiguration.notificationsTableName)
        createCommunicationConfirmationsTable(dynamoDbClient, dbConfiguration.communicationConfirmationsTableName)
        return dynamoDbClient
    }

    private fun createNotificationsTable(dynamoDbClient: DynamoDbClient, tableName: String) {
        if (dynamoDbClient.listTables().tableNames().contains(tableName)) {
            return
        }

        val attributeDefinitions: MutableList<AttributeDefinition> = mutableListOf(
            attributeDefinition("id"),
            attributeDefinition("sourceReference"),
        )

        val keySchema = listOf(partitionKey("id"))

        val indexKeySchema = listOf(partitionKey("sourceReference"))

        val indexSchema = GlobalSecondaryIndex.builder()
            .indexName(SOURCE_REFERENCE_INDEX_NAME)
            .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(0L).writeCapacityUnits(0L).build())
            .keySchema(indexKeySchema)
            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
            .build()

        val request: CreateTableRequest = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(keySchema)
            .globalSecondaryIndexes(indexSchema)
            .attributeDefinitions(attributeDefinitions)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build()

        dynamoDbClient.createTable(request)
    }

    private fun createCommunicationConfirmationsTable(dynamoDbClient: DynamoDbClient, tableName: String) {
        if (dynamoDbClient.listTables().tableNames().contains(tableName)) {
            return
        }

        val attributeDefinitions: MutableList<AttributeDefinition> = mutableListOf(
            attributeDefinition("id"),
            attributeDefinition("sourceReference"),
        )

        val keySchema = listOf(partitionKey("id"))

        val indexKeySchema = listOf(partitionKey("sourceReference"))

        val indexSchema = GlobalSecondaryIndex.builder()
            .indexName(COMMUNICATION_CONFIRMATION_SOURCE_REFERENCE_INDEX_NAME)
            .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(0L).writeCapacityUnits(0L).build())
            .keySchema(indexKeySchema)
            .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
            .build()

        val request: CreateTableRequest = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(keySchema)
            .globalSecondaryIndexes(indexSchema)
            .attributeDefinitions(attributeDefinitions)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build()

        dynamoDbClient.createTable(request)
    }

    private fun attributeDefinition(name: String): AttributeDefinition =
        AttributeDefinition.builder().attributeName(name).attributeType("S").build()

    private fun partitionKey(name: String): KeySchemaElement =
        KeySchemaElement.builder().attributeName(name).keyType(KeyType.HASH).build()

    private fun sortKey(name: String): KeySchemaElement =
        KeySchemaElement.builder().attributeName(name).keyType(KeyType.RANGE).build()

    private fun GenericContainer<*>.getEndpointOverride(): URI? {
        val ipAddress = InetAddress.getByName(host).hostAddress
        val mappedPort = getMappedPort(DEFAULT_PORT)
        return URI("http://$ipAddress:$mappedPort")
    }
}

data class LocalStackContainerSettings(
    val apiUrl: String,
    val sendUkGovNotifyPhotoResubmissionQueueName: String,
    val sendUkGovNotifyIdDocumentResubmissionQueueName: String,
    val sendUkGovNotifyIdDocumentRequiredMessageQueueName: String,
    val sendUkGovNotifyApplicationReceivedQueueName: String,
    val sendUkGovNotifyApplicationApprovedQueueName: String,
    val sendUkGovNotifyApplicationRejectedMessageQueueName: String,
    val sendUkGovNotifyRejectedDocumentMessageQueueName: String,
    val sendUkGovNotifyNinoNotMatchedMessageQueueName: String,
    val removeApplicationNotificationsQueueName: String,
    val triggerVoterCardStatisticsUpdateQueueName: String,
) {
    val mappedQueueUrlSendUkGovNotifyPhotoResubmissionQueueName: String = toMappedUrl(sendUkGovNotifyPhotoResubmissionQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyIdDocumentResubmissionQueueName: String = toMappedUrl(sendUkGovNotifyIdDocumentResubmissionQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyIdDocumentRequiredQueueName: String = toMappedUrl(sendUkGovNotifyIdDocumentRequiredMessageQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyApplicationReceivedQueueName: String = toMappedUrl(sendUkGovNotifyApplicationReceivedQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyApplicationApprovedQueueName: String = toMappedUrl(sendUkGovNotifyApplicationApprovedQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyApplicationRejectedMessageQueueName: String = toMappedUrl(sendUkGovNotifyApplicationRejectedMessageQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyRejectedDocumentMessageQueueName: String = toMappedUrl(sendUkGovNotifyRejectedDocumentMessageQueueName, apiUrl)
    val mappedQueueUrlRemoveApplicationNotificationsQueueName: String = toMappedUrl(removeApplicationNotificationsQueueName, apiUrl)
    val mappedQueueUrlSendUkGovNotifyNinoNotMatchedMessageQueueName: String = toMappedUrl(sendUkGovNotifyNinoNotMatchedMessageQueueName, apiUrl)
    val mappedQueueUrlTriggerVoterCardStatisticsUpdateQueueName: String = toMappedUrl(triggerVoterCardStatisticsUpdateQueueName, apiUrl)

    private fun toMappedUrl(rawUrlString: String, apiUrlString: String): String {
        val rawUrl = URI.create(rawUrlString)
        val apiUrl = URI.create(apiUrlString)
        return URI(
            rawUrl.scheme,
            rawUrl.userInfo,
            apiUrl.host,
            apiUrl.port,
            rawUrl.path,
            rawUrl.query,
            rawUrl.fragment
        ).toASCIIString()
    }
}
