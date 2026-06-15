package uk.gov.dluhc.notificationsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

// TODO EIP1-13474: Test todo checker with old todo that is done

/**
 * Spring Boot application bootstrapping class.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class NotificationsApiApplication

fun main(args: Array<String>) {
    runApplication<NotificationsApiApplication>(*args)
}
