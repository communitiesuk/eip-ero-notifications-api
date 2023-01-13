package uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity

import org.assertj.core.api.AbstractAssert
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import java.util.UUID

/**
 * AssertJ Assertion class for [NotifyDetails] entity classes.
 */
class NotifyDetailsAssert(actual: NotifyDetails?) :
    AbstractAssert<NotifyDetailsAssert, NotifyDetails?>(actual, NotifyDetailsAssert::class.java) {

    companion object {
        fun assertThat(actual: NotifyDetails?) = NotifyDetailsAssert(actual)
    }

    fun hasNotificationId(expected: UUID?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (notificationId != expected) {
                failWithMessage("Expected notificationId $expected, but was $notificationId")
            }
        }
        return this
    }

    fun hasReference(expected: String?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (reference != expected) {
                failWithMessage("Expected reference $expected, but was $reference")
            }
        }
        return this
    }

    fun hasTemplateId(expected: UUID?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (templateId != expected) {
                failWithMessage("Expected templateId $expected, but was $templateId")
            }
        }
        return this
    }

    fun hasTemplateVersion(expected: Int?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (templateVersion != expected) {
                failWithMessage("Expected templateVersion $expected, but was $templateVersion")
            }
        }
        return this
    }

    fun hasTemplateUri(expected: String?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (templateUri != expected) {
                failWithMessage("Expected templateUri $expected, but was $templateUri")
            }
        }
        return this
    }

    fun hasBody(expected: String?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (body != expected) {
                failWithMessage("Expected body $expected, but was $body")
            }
        }
        return this
    }

    fun hasSubject(expected: String?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (subject != expected) {
                failWithMessage("Expected subject $expected, but was $subject")
            }
        }
        return this
    }

    fun hasFromEmail(expected: String?): NotifyDetailsAssert {
        isNotNull
        with(actual!!) {
            if (fromEmail != expected) {
                failWithMessage("Expected fromEmail $expected, but was $fromEmail")
            }
        }
        return this
    }
}
