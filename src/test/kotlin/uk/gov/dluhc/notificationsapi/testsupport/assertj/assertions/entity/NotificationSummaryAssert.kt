package uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity

import org.assertj.core.api.AbstractAssert
import uk.gov.dluhc.notificationsapi.database.entity.NotificationSummary
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import java.time.LocalDateTime
import java.util.UUID

/**
 * AssertJ Assertion class for [NotificationSummary] entity classes.
 */
class NotificationSummaryAssert(actual: NotificationSummary?) :
    AbstractAssert<NotificationSummaryAssert, NotificationSummary?>(actual, NotificationSummaryAssert::class.java) {

    companion object {
        fun assertThat(actual: NotificationSummary?) = NotificationSummaryAssert(actual)
    }

    fun hasId(expected: UUID?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (id != expected) {
                failWithMessage("Expected id $expected, but was $id")
            }
        }
        return this
    }

    fun hasGssCode(expected: String?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (gssCode != expected) {
                failWithMessage("Expected gssCode $expected, but was $gssCode")
            }
        }
        return this
    }

    fun hasType(expected: NotificationType?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (type != expected) {
                failWithMessage("Expected type $expected, but was $type")
            }
        }
        return this
    }

    fun hasRequestor(expected: String?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (requestor != expected) {
                failWithMessage("Expected requestor $expected, but was $requestor")
            }
        }
        return this
    }

    fun hasSourceReference(expected: String?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (sourceReference != expected) {
                failWithMessage("Expected sourceReference $expected, but was $sourceReference")
            }
        }
        return this
    }

    fun hasSentAt(expected: LocalDateTime?): NotificationSummaryAssert {
        isNotNull
        with(actual!!) {
            if (sentAt != expected) {
                failWithMessage("Expected sentAt $expected, but was $sentAt")
            }
        }
        return this
    }
}
