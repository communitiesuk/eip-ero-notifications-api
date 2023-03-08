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

@ConfigurationProperties(prefix = "api.notify.template.email")
@ConstructorBinding
data class NotifyEmailTemplateConfiguration(
    val receivedEnglish: String,
    val receivedWelsh: String,
    val postalReceivedEnglish: String,
    val postalReceivedWelsh: String,
    val approvedEnglish: String,
    val approvedWelsh: String,
    val photoResubmissionEnglish: String,
    val photoResubmissionWelsh: String,
    val idDocumentResubmissionEnglish: String,
    val idDocumentResubmissionWelsh: String,
)

@ConfigurationProperties(prefix = "api.notify.template.letter")
@ConstructorBinding
data class NotifyLetterTemplateConfiguration(
    val receivedEnglish: String,
    val receivedWelsh: String,
    val rejectedEnglish: String,
    val rejectedWelsh: String,
    val photoResubmissionEnglish: String,
    val photoResubmissionWelsh: String,
    val idDocumentResubmissionEnglish: String,
    val idDocumentResubmissionWelsh: String,
)
