package uk.gov.dluhc.notificationsapi.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WiremockConfiguration {

    var baseUrl: String? = null

    @Bean
    fun wireMockServer(applicationContext: ConfigurableApplicationContext): WireMockServer =
        WireMockServer(
            WireMockConfiguration.options()
                .dynamicPort()
                .dynamicHttpsPort()
        ).apply {
            start()
            baseUrl = "http://localhost:${this.port()}"
        }
}
