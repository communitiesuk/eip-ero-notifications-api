package uk.gov.dluhc.notificationsapi.config

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
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput
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
                        "SERVICES" to "dynamodb",
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

    @Primary
    @Bean
    fun testDynamoDbClient(testAwsCredentialsProvider: AwsCredentialsProvider, dbConfiguration: DynamoDbConfiguration): DynamoDbClient {
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

        val attributeDefinitions: MutableList<AttributeDefinition> = mutableListOf()
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("id").attributeType("S").build())
        attributeDefinitions.add(AttributeDefinition.builder().attributeName("gssCode").attributeType("S").build())

        val keySchema: MutableList<KeySchemaElement> = mutableListOf()
        keySchema.add(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
        keySchema.add(KeySchemaElement.builder().attributeName("gssCode").keyType(KeyType.RANGE).build())

        val request: CreateTableRequest = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(keySchema)
            .attributeDefinitions(attributeDefinitions)
            .provisionedThroughput(
                ProvisionedThroughput.builder()
                    .readCapacityUnits(5L)
                    .writeCapacityUnits(6L)
                    .build()
            ).build()

        dynamoDbClient.createTable(request)
    }

    private fun GenericContainer<*>.getEndpointOverride(): URI? {
        val ipAddress = InetAddress.getByName(host).hostAddress
        val mappedPort = getMappedPort(DEFAULT_PORT)
        return URI("http://$ipAddress:$mappedPort")
    }
}
