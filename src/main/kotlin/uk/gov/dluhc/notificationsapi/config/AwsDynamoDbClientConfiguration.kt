package uk.gov.dluhc.notificationsapi.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

@Configuration
class AwsDynamoDbClientConfiguration {

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()

    @Bean
    fun dynamoDbClient(dynamoDbConfiguration: DynamoDbConfiguration): DynamoDbClient {
        val builder = DynamoDbClient.builder().credentialsProvider(DefaultCredentialsProvider.create())

        if (dynamoDbConfiguration.endpoint != null) {
            builder.endpointOverride(dynamoDbConfiguration.endpoint)
        }

        return builder.build()
    }
}

@ConfigurationProperties(prefix = "dynamodb")
data class DynamoDbConfiguration(
    val notificationsTableName: String,
    val communicationConfirmationsTableName: String,
    val notificationAuditsTableName: String,
    val endpoint: URI?,
)
