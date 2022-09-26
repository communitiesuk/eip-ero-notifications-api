package uk.gov.dluhc.notificationsapi.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
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
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient
import uk.gov.dluhc.aws.cognito.AsyncCognitoService
import java.net.URI

/**
 * Configuration class exposing beans for the LocalStack (AWS) environment.
 */
@Configuration
class LocalStackContainerConfiguration {

    private companion object {
        // TODO the default region should be eu-west-2 but it currently causes the build to fail...
        const val DEFAULT_REGION = "us-east-1"
        const val DEFAULT_PORT = 4566
        const val DEFAULT_ACCESS_KEY_ID = "test"
        const val DEFAULT_SECRET_KEY = "test"

        val objectMapper = ObjectMapper()
    }

    @Bean
    fun awsBasicCredentialsProvider(): AwsCredentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(DEFAULT_ACCESS_KEY_ID, DEFAULT_SECRET_KEY))

    @Bean
    fun cognitoIdentityProviderAsyncClient(
        localStackContainerSettings: LocalStackContainerSettings,
        awsCredentialsProvider: AwsCredentialsProvider
    ): CognitoIdentityProviderAsyncClient =
        CognitoIdentityProviderAsyncClient.builder()
            .endpointOverride(URI.create(localStackContainerSettings.apiUrl))
            .credentialsProvider(awsCredentialsProvider)
            .build()

    @Bean
    fun asyncCognitoService(cognitoAsyncClient: CognitoIdentityProviderAsyncClient): AsyncCognitoService =
        AsyncCognitoService(cognitoAsyncClient)

    /**
     * Creates and starts LocalStack configured with a basic (empty) SQS service.
     * Returns the container that can subsequently be used for further setup and configuration.
     */
    @Bean
    fun localstackContainer(
        @Value("\${cloud.aws.region.static}") region: String,
        @Value("\${localstack.api.key}") localStackApiKey: String
    ): GenericContainer<*> {
        return GenericContainer(
            DockerImageName.parse("localstack/localstack:latest")
        ).withEnv(
            mapOf(
                "SERVICES" to "cognito, sqs, s3, sts",
                "AWS_DEFAULT_REGION" to region,
                "LOCALSTACK_API_KEY" to localStackApiKey
            )
        ).withExposedPorts(DEFAULT_PORT)
            .apply {
                start()
            }
    }

    /**
     * Uses the localstack container to configure the various services.
     *
     * @return a [LocalStackContainerSettings] bean encapsulating the various IDs etc of the configured container and services.
     */
    @Bean
    fun localStackContainerSqsSettings(
        @Qualifier("localstackContainer") localStackContainer: GenericContainer<*>,
        applicationContext: ConfigurableApplicationContext,
    ): LocalStackContainerSettings {
        val eroUserPoolId = localStackContainer.createCognitoUserPool("ero")
        val eroClientId = localStackContainer.createCognitoUserPoolClient(eroUserPoolId, "ero")

        val apiUrl = "http://${localStackContainer.host}:${localStackContainer.getMappedPort(DEFAULT_PORT)}"

        TestPropertyValues.of(
            "cloud.aws.sqs.endpoint=$apiUrl",
            "spring.security.oauth2.resourceserver.jwt.issuer-uri=$apiUrl/$eroUserPoolId/.well-known/jwks.json",
        ).applyTo(applicationContext)

        return LocalStackContainerSettings(
            apiUrl = apiUrl,
            cognito = Cognito(eroUserPoolId, eroClientId)
        )
    }

    private fun GenericContainer<*>.createCognitoUserPool(userPoolName: String): String {
        return execInContainer(
            "awslocal", "cognito-idp", "create-user-pool", "--pool-name", userPoolName
        ).stdout.let {
            objectMapper.readValue(it, Map::class.java)
        }.let {
            (it["UserPool"] as Map<String, String>)["Id"]!!
        }
    }

    private fun GenericContainer<*>.createCognitoUserPoolClient(userPoolId: String, userPoolName: String): String =
        execInContainer(
            "awslocal",
            "cognito-idp",
            "create-user-pool-client",
            "--client-name",
            "$userPoolName-client",
            "--user-pool-id",
            userPoolId
        ).stdout.let {
            objectMapper.readValue(it, Map::class.java)
        }.let {
            (it["UserPoolClient"] as Map<String, String>)["ClientId"]!!
        }

    @Bean
    @Primary
    fun localStackStsClient(@Qualifier("localstackContainer") localStackContainer: GenericContainer<*>): AWSSecurityTokenService =
        AWSSecurityTokenServiceClient.builder()
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(DEFAULT_ACCESS_KEY_ID, DEFAULT_SECRET_KEY)
                )
            )
            .withEndpointConfiguration(
                EndpointConfiguration(
                    "http://${localStackContainer.host}:${localStackContainer.getMappedPort(DEFAULT_PORT)}",
                    DEFAULT_REGION
                )
            )
            .build()
}

data class LocalStackContainerSettings(
    val apiUrl: String,
    val cognito: Cognito,
)

data class Cognito(
    val userPoolId: String,
    val clientId: String,
)
