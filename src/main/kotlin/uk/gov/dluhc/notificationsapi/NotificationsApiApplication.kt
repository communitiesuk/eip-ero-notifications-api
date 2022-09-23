package uk.gov.dluhc.notificationsapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Spring Boot application bootstrapping class.
 */
@SpringBootApplication
class NotificationsApiApplication

fun main(args: Array<String>) {
    runApplication<NotificationsApiApplication>(*args)
}
