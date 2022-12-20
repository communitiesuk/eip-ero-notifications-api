package uk.gov.dluhc.notificationsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.service.notify.NotificationClient

@Configuration
class NotifyClientConfiguration {

    @Bean
    fun notificationClient(@Value("\${api.notify.api-key}") apiKey: String) = NotificationClient(apiKey)
}

@ConfigurationProperties(prefix = "api.notify.template")
@ConstructorBinding
data class NotifyTemplateConfiguration(
    val receivedEmailEnglish: String,
    val receivedEmailWelsh: String,
    val approvedEmailEnglish: String,
    val approvedEmailWelsh: String,
    val rejectedEmailEnglish: String,
    val rejectedEmailWelsh: String,
    val photoResubmissionEmailEnglish: String,
    val photoResubmissionEmailWelsh: String
)
