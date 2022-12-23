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
import uk.gov.dluhc.notificationsapi.database.entity.Notification.Companion.SOURCE_REFERENCE_INDEX_NAME
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
        @Value("\${sqs.send-uk-gov-notify-photo-resubmission-message-queue-name}") sendUkGovNotifyPhotoResubmissionMessageQueueName: String,
    ): LocalStackContainerSettings {
        val sendUkGovNotifyPhotoResubmissionQueueName = localStackContainer.createSqsQueue(sendUkGovNotifyPhotoResubmissionMessageQueueName)

        val apiUrl = "http://${localStackContainer.host}:${localStackContainer.getMappedPort(DEFAULT_PORT)}"

        TestPropertyValues.of("cloud.aws.sqs.endpoint=$apiUrl").applyTo(applicationContext)

        return LocalStackContainerSettings(
            apiUrl = apiUrl,
            sendUkGovNotifyPhotoResubmissionQueueName = sendUkGovNotifyPhotoResubmissionQueueName,
        )
    }

    private fun GenericContainer<*>.createSqsQueue(queueName: String): String {
        val execInContainer = execInContainer(
            "awslocal", "sqs", "create-queue", "--queue-name", queueName,
            "--attributes", "VisibilityTimeout=1,MessageRetentionPeriod=5"
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
        return dynamoDbClient
    }

    private fun createNotificationsTable(dynamoDbClient: DynamoDbClient, tableName: String) {
        if (dynamoDbClient.listTables().tableNames().contains(tableName)) {
            return
        }

        val attributeDefinitions: MutableList<AttributeDefinition> = mutableListOf(
            attributeDefinition("id"),
            attributeDefinition("gssCode"),
            attributeDefinition("sourceReference"),
        )

        val keySchema: MutableList<KeySchemaElement> = mutableListOf(partitionKey("id"), sortKey("gssCode"))

        val indexKeySchema: MutableList<KeySchemaElement> =
            mutableListOf(partitionKey("sourceReference"), sortKey("gssCode"))

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
)
