package uk.gov.dluhc.notificationsapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import uk.gov.dluhc.notificationsapi.testsupport.WiremockService
import uk.gov.service.notify.NotificationClient

@Configuration
class TestNotifyClientConfiguration {

    @Bean
    @Primary
    fun notificationClient(@Value("\${api.notify.api-key}") apiKey: String, wiremockService: WiremockService) =
        NotificationClient(apiKey, wiremockService.wiremockBaseUrl())
}