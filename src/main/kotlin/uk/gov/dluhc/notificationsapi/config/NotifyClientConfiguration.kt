package uk.gov.dluhc.notificationsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.service.notify.NotificationClient

@Configuration
class NotifyClientConfiguration {

    @Bean
    fun notificationClient(@Value("\${api.notify.api-key}") apiKey: String) = NotificationClient(apiKey)
}
