package uk.gov.dluhc.notificationsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Spring Boot application bootstrapping class.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class NotificationsApiApplication

fun main(args: Array<String>) {
    runApplication<NotificationsApiApplication>(*args)
}
