package uk.gov.dluhc.notificationsapi.config

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import uk.gov.dluhc.notificationsapi.config.IntegrationTest.Companion.DEFAULT_PORT
import uk.gov.dluhc.notificationsapi.config.IntegrationTest.Companion.DEFAULT_REGION
import uk.gov.dluhc.notificationsapi.config.IntegrationTest.Companion.LOCALSTACK_API_KEY

class LocalStackContainerConfiguration : GenericContainer<LocalStackContainerConfiguration>(LOCALSTACK_IMAGE) {

    companion object {
        private val LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest")
        private var container: LocalStackContainerConfiguration? = null

        /**
         * Creates and starts LocalStack configured with a basic (empty) SQS service.
         * Returns the container that can subsequently be used for further setup and configuration.
         */
        fun getInstance(): LocalStackContainerConfiguration {
            if (container == null) {
                container = LocalStackContainerConfiguration()
                    .withEnv(
                        mapOf(
                            "SERVICES" to "cognito, sqs, s3, sts",
                            "AWS_DEFAULT_REGION" to DEFAULT_REGION,
                            "LOCALSTACK_API_KEY" to LOCALSTACK_API_KEY
                        )
                    ).withExposedPorts(DEFAULT_PORT)
                    .apply {
                        withReuse(true)
                        start()
                    }
            }
            return container!!
        }
    }
}

data class LocalStackContainerSettings(
    val apiUrl: String,
    val cognito: Cognito,
)

data class Cognito(
    val userPoolId: String,
    val clientId: String,
)
