package uk.gov.dluhc.notificationsapi.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.securitytoken.AWSSecurityTokenService
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient
import uk.gov.dluhc.aws.cognito.AsyncCognitoService
import uk.gov.dluhc.notificationsapi.testsupport.LocalStackCognitoUserService
import java.net.URI

/**
 * Base class used to bring up the entire Spring ApplicationContext
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [IntegrationTest.IntegrationTestConfiguration::class]
)
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "PT5M")
internal abstract class IntegrationTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var localStackCognitoUserService: LocalStackCognitoUserService

    companion object {
        private const val DEFAULT_ACCESS_KEY_ID = "test"
        private const val DEFAULT_SECRET_KEY = "test"
        // TODO the default region should be eu-west-2 but it currently causes the build to fail...
        const val DEFAULT_REGION = "us-east-1"
        const val DEFAULT_PORT = 4566
        private val objectMapper = ObjectMapper()
        val LOCALSTACK_API_KEY: String = System.getenv("LOCALSTACK_API_KEY")
        val localStackContainer: LocalStackContainerConfiguration = LocalStackContainerConfiguration.getInstance()
    }

    @TestConfiguration
    class IntegrationTestConfiguration {
        @Bean
        fun awsBasicCredentialsProvider(): AwsCredentialsProvider =
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    DEFAULT_ACCESS_KEY_ID,
                    DEFAULT_SECRET_KEY
                )
            )

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
         * Uses the localstack container to configure the various services.
         *
         * @return a [LocalStackContainerSettings] bean encapsulating the various IDs etc of the configured container and services.
         */
        @Bean
        fun localStackContainerSqsSettings(
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
        fun localStackStsClient(): AWSSecurityTokenService =
            AWSSecurityTokenServiceClient.builder()
                .withCredentials(
                    AWSStaticCredentialsProvider(
                        BasicAWSCredentials(DEFAULT_ACCESS_KEY_ID, DEFAULT_SECRET_KEY)
                    )
                )
                .withEndpointConfiguration(
                    AwsClientBuilder.EndpointConfiguration(
                        "http://${localStackContainer.host}:${
                            localStackContainer.getMappedPort(
                                DEFAULT_PORT
                            )
                        }",
                        DEFAULT_REGION
                    )
                )
                .build()
    }
}
