package uk.gov.dluhc.notificationsapi.config

import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.InetAddress
import java.net.URI

private val logger = KotlinLogging.logger {}

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
                    DockerImageName.parse("localstack/localstack:latest")
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

    @Primary
    @Bean
    fun testDynamoDbClient(testAwsCredentialsProvider: AwsCredentialsProvider): DynamoDbClient =
        DynamoDbClient.builder()
            .credentialsProvider(testAwsCredentialsProvider)
            .endpointOverride(localStackContainer.getEndpointOverride())
            .build()

    @Bean
    fun awsBasicCredentialsProvider(): AwsCredentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(DEFAULT_ACCESS_KEY_ID, DEFAULT_SECRET_KEY))

    @Bean
    fun createDynamoDbTables() {
        localStackContainer.createTable(
            """
            {"TableName":"unit-test-notifications", 
            "KeySchema":[{"AttributeName":"id","KeyType":"HASH"}], 
            "AttributeDefinitions":[{"AttributeName":"id","AttributeType":"S"}],
            "BillingMode":"PAY_PER_REQUEST"}
            """.trimIndent()
        )
    }

    private fun GenericContainer<*>.createTable(tableDefinition: String) {
        execInContainer(
            "awslocal", "dynamodb", "create-table", "--cli-input-json", tableDefinition
        ).let {
            if (it.exitCode != 0) {
                logger.error("failed to create table with $it.exitCode : $it.stderr")
            }
        }
    }

    private fun GenericContainer<*>.getEndpointOverride(): URI? {
        val ipAddress = InetAddress.getByName(host).hostAddress
        val mappedPort = getMappedPort(DEFAULT_PORT)
        return URI("http://$ipAddress:$mappedPort")
    }
}
