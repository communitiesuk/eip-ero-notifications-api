package uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.within
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID

/**
 * AssertJ Assertion class for [CommunicationConfirmation] entity classes.
 *
 * In addition to regular assertion methods on the CommunicationConfirmation, this class provides lambda functions to allow
 * chaining into child entities in a fluent manner. For example:
 * ```
 * val communicationConfirmation = ....
 * assertThat(communicationConfirmation)
 *     .hasGssCode(expectedGssCode)
 *     .hasSourceReference(expectedSourceReference)
 * ```
 * The test can mix and match the assertions as necessary.
 */
class CommunicationConfirmationAssert(actual: CommunicationConfirmation?) :
    AbstractAssert<CommunicationConfirmationAssert, CommunicationConfirmation?>(actual, CommunicationConfirmationAssert::class.java) {

    companion object {
        fun assertThat(actual: CommunicationConfirmation?) = CommunicationConfirmationAssert(actual)
    }

    fun hasId(expected: UUID?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (id != expected) {
                failWithMessage("Expected id $expected, but was $id")
            }
        }
        return this
    }

    fun hasGssCode(expected: String?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (gssCode != expected) {
                failWithMessage("Expected gssCode $expected, but was $gssCode")
            }
        }
        return this
    }

    fun hasReason(expected: CommunicationConfirmationReason?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (reason != expected) {
                failWithMessage("Expected reason $expected, but was $reason")
            }
        }
        return this
    }

    fun hasChannel(expected: CommunicationConfirmationChannel?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (channel != expected) {
                failWithMessage("Expected channel $expected, but was $channel")
            }
        }
        return this
    }

    fun hasRequestor(expected: String?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (requestor != expected) {
                failWithMessage("Expected requestor $expected, but was $requestor")
            }
        }
        return this
    }

    fun hasSourceReference(expected: String?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (sourceReference != expected) {
                failWithMessage("Expected sourceReference $expected, but was $sourceReference")
            }
        }
        return this
    }

    fun hasSourceType(expected: SourceType?): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (sourceType != expected) {
                failWithMessage("Expected sourceType $expected, but was $sourceType")
            }
        }
        return this
    }

    fun sentAtIsCloseTo(expected: LocalDateTime, secondsOffsetAllowed: Long): CommunicationConfirmationAssert {
        isNotNull
        with(actual!!) {
            if (sentAt == null) {
                failWithMessage("Expecting sentAt not to be null")
            }

            Assertions.assertThat(sentAt)
                .`as`(
                    String.format(
                        "%nExpecting sentAt:%n  $actual%nto be close to:%n  $expected%nwithin " +
                            "$secondsOffsetAllowed seconds but was ${expected.until(sentAt, SECONDS)}"
                    )
                )
                .isCloseTo(expected, within(secondsOffsetAllowed, SECONDS))
        }
        return this
    }
}
