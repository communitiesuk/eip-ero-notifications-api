package uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity

import org.assertj.core.api.AbstractAssert
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import java.time.LocalDateTime
import java.util.UUID
import java.util.function.Consumer

/**
 * AssertJ Assertion class for [Notification] entity classes.
 *
 * In addition to regular assertion methods on the Notification, this class provides lambda functions to allow
 * chaining into child entities in a fluent manner. For example:
 * ```
 * val notification = ....
 * assertThat(notification)
 *     .hasGssCode(expectedGssCode)
 *     .notifyDetails {
 *         it.hasNotificationId(expectedNotificationId)
 *           .hasReference(expectedReference)
 *     }
 * ```
 * The test can mix and match the assertions as necessary.
 */
class NotificationAssert(actual: Notification?) :
    AbstractAssert<NotificationAssert, Notification?>(actual, NotificationAssert::class.java) {

    companion object {
        fun assertThat(actual: Notification?) = NotificationAssert(actual)
    }

    fun hasId(expected: UUID?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (id != expected) {
                failWithMessage("Expected id $expected, but was $id")
            }
        }
        return this
    }

    fun hasGssCode(expected: String?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (gssCode != expected) {
                failWithMessage("Expected gssCode $expected, but was $gssCode")
            }
        }
        return this
    }

    fun hasType(expected: NotificationType?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (type != expected) {
                failWithMessage("Expected type $expected, but was $type")
            }
        }
        return this
    }

    fun hasToEmail(expected: String?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (toEmail != expected) {
                failWithMessage("Expected toEmail $expected, but was $toEmail")
            }
        }
        return this
    }

    fun hasRequestor(expected: String?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (requestor != expected) {
                failWithMessage("Expected requestor $expected, but was $requestor")
            }
        }
        return this
    }

    fun hasSourceReference(expected: String?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (sourceReference != expected) {
                failWithMessage("Expected sourceReference $expected, but was $sourceReference")
            }
        }
        return this
    }

    fun hasPersonalisation(expected: Map<String, String>?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (personalisation != expected) {
                failWithMessage("Expected personalisation $expected, but was $personalisation")
            }
        }
        return this
    }

    fun hasSentAt(expected: LocalDateTime?): NotificationAssert {
        isNotNull
        with(actual!!) {
            if (sentAt != expected) {
                failWithMessage("Expected sentAt $expected, but was $sentAt")
            }
        }
        return this
    }

    /**
     * Allows for assertion chaining into the child [NotifyDetails] entity. Takes a lambda as the method argument
     * to call assertion methods provided by [NotifyDetailsAssert].
     * Returns this [NotificationAssert] to allow further chained assertions on the parent [Notification]
     */
    fun notifyDetails(consumer: Consumer<NotifyDetailsAssert>): NotificationAssert {
        isNotNull
        with(actual!!) {
            consumer.accept(NotifyDetailsAssert.assertThat(notifyDetails))
        }
        return this
    }
}
