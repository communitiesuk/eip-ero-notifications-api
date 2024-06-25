package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.NotificationNotFoundException
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity.NotificationAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity.NotificationSummaryAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anotherGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntitySourceType
import java.util.UUID

internal class NotificationRepositoryIntegrationTest : IntegrationTest() {

    @Nested
    inner class GetNotificationById {
        @Test
        fun `should save and get notification by notification id`() {
            // Given
            val id = aNotificationId()
            val gssCode = aGssCode()
            val type = anEntityNotificationType()
            val channel = anEntityChannel()
            val toEmail = anEmailAddress()
            val requestor = aRequestor()
            val sourceReference = aSourceReference()
            val sourceType = anEntitySourceType()
            val personalisation = aNotificationPersonalisationMap()
            val notifyDetails = aNotifyDetails()
            val sentAt = aLocalDateTime()
            val notification = Notification(
                id = id,
                gssCode = gssCode,
                type = type,
                toEmail = toEmail,
                requestor = requestor,
                sourceReference = sourceReference,
                sourceType = sourceType,
                channel = channel,
                personalisation = personalisation,
                notifyDetails = notifyDetails,
                sentAt = sentAt,
            )

            // When
            notificationRepository.saveNotification(notification)

            // Then
            val fetchedNotification = notificationRepository.getNotification(id)
            assertThat(fetchedNotification)
                .hasId(id)
                .hasGssCode(gssCode)
                .hasType(type)
                .hasToEmail(toEmail)
                .hasRequestor(requestor)
                .hasSourceReference(sourceReference)
                .hasPersonalisation(personalisation)
                .hasSentAt(sentAt)
                .notifyDetails {
                    it.isEqualTo(notifyDetails)
                }
        }

        @Test
        fun `should fail to get notification by id that does not exist`() {
            // Given
            val id = UUID.fromString("1873fb59-c15d-4473-8ff5-12076b102155")
            val msg = "Notification item not found for id: 1873fb59-c15d-4473-8ff5-12076b102155"

            // When
            val ex = catchThrowableOfType(
                { notificationRepository.getNotification(id) },
                NotificationNotFoundException::class.java,
            )

            // Then
            assertThat(ex).hasMessage(msg)
        }
    }

    @Nested
    inner class GetNotificationBySourceReference {

        @Test
        fun `should get notification by source reference`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aSourceReference()
            val id = aRandomNotificationId()
            val type = anEntityNotificationType()
            val channel = anEntityChannel()
            val toEmail = anEmailAddress()
            val requestor = aRequestor()
            val sourceType = anEntitySourceType()
            val personalisation = aNotificationPersonalisationMap()
            val notifyDetails = aNotifyDetails()
            val sentAt = aLocalDateTime()
            val notification = Notification(
                id = id,
                gssCode = gssCode,
                type = type,
                toEmail = toEmail,
                requestor = requestor,
                sourceReference = sourceReference,
                sourceType = sourceType,
                channel = channel,
                personalisation = personalisation,
                notifyDetails = notifyDetails,
                sentAt = sentAt,
            )
            notificationRepository.saveNotification(notification)

            // When
            val fetchedNotificationList = notificationRepository.getBySourceReferenceAndGssCode(sourceReference, sourceType, listOf(gssCode))

            // Then
            assertThat(fetchedNotificationList).hasSize(1)
            val fetchedNotification = fetchedNotificationList[0]
            assertThat(fetchedNotification)
                .hasId(id)
                .hasGssCode(gssCode)
                .hasType(type)
                .hasToEmail(toEmail)
                .hasRequestor(requestor)
                .hasSourceReference(sourceReference)
                .hasPersonalisation(personalisation)
                .hasSentAt(sentAt)
                .notifyDetails {
                    it.isEqualTo(notifyDetails)
                }
        }

        @Test
        fun `should find no notifications for source reference that does not exist`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aRandomSourceReference()
            val sourceType = anEntitySourceType()
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    sourceReference = sourceReference,
                    sourceType = sourceType,
                    gssCode = gssCode,
                ),
            )

            val otherSourceReference = aRandomSourceReference()

            // When
            val fetchedNotificationList = notificationRepository.getBySourceReferenceAndGssCode(otherSourceReference, sourceType, listOf(gssCode))

            // Then
            assertThat(fetchedNotificationList).isEmpty()
        }
    }

    @Nested
    inner class RemoveNotificationBySourceReference {

        @Test
        fun `should remove notifications by source reference`() {
            // Given
            val gssCode1 = aGssCode()
            val gssCode2 = anotherGssCode()
            val sourceReference = aRandomSourceReference()
            val sourceType = anEntitySourceType()
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    gssCode = gssCode1,
                    sourceReference = sourceReference,
                    sourceType = sourceType,
                ),
            )
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    gssCode = gssCode2,
                    sourceReference = sourceReference,
                    sourceType = sourceType,
                ),
            )

            // When
            notificationRepository.removeBySourceReference(sourceReference, sourceType)

            // Then
            val fetchedNotificationList = notificationRepository.getBySourceReferenceAndGssCode(sourceReference, sourceType, listOf(gssCode1, gssCode2))
            assertThat(fetchedNotificationList).isEmpty()
        }
    }

    @Nested
    inner class GetNotificationSummariesBySourceReference {

        @Test
        fun `should get notification summaries by source reference and source type`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aSourceReference()
            val id = aRandomNotificationId()
            val type = anEntityNotificationType()
            val channel = anEntityChannel()
            val toEmail = anEmailAddress()
            val requestor = aRequestor()
            val sourceType = anEntitySourceType()
            val personalisation = aNotificationPersonalisationMap()
            val notifyDetails = aNotifyDetails()
            val sentAt = aLocalDateTime()
            val notification = Notification(
                id = id,
                gssCode = gssCode,
                type = type,
                toEmail = toEmail,
                requestor = requestor,
                sourceReference = sourceReference,
                sourceType = sourceType,
                channel = channel,
                personalisation = personalisation,
                notifyDetails = notifyDetails,
                sentAt = sentAt,
            )
            notificationRepository.saveNotification(notification)

            // When
            val fetchedNotificationList = notificationRepository.getNotificationSummariesBySourceReference(
                sourceReference,
                VOTER_CARD,
                listOf(gssCode),
            )

            // Then
            assertThat(fetchedNotificationList).hasSize(1)
            val fetchedNotification = fetchedNotificationList[0]
            assertThat(fetchedNotification)
                .hasId(id)
                .hasSourceReference(sourceReference)
                .hasGssCode(gssCode)
                .hasType(type)
                .hasRequestor(requestor)
                .hasSentAt(sentAt)
        }

        @Test
        fun `should find no notification summaries by source reference and source type given source reference that does not exist`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aRandomSourceReference()
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    sourceReference = sourceReference,
                    sourceType = VOTER_CARD,
                    gssCode = gssCode,
                ),
            )

            val otherSourceReference = aRandomSourceReference()

            // When
            val fetchedNotificationList = notificationRepository.getNotificationSummariesBySourceReference(
                otherSourceReference,
                VOTER_CARD,
                listOf(gssCode),
            )

            // Then
            assertThat(fetchedNotificationList).isEmpty()
        }

        @Test
        fun `should find no notification summaries by source reference and source type given no matches in the specified gssCodes`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aRandomSourceReference()
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    sourceReference = sourceReference,
                    sourceType = VOTER_CARD,
                    gssCode = gssCode,
                ),
            )

            val otherGssCodes = listOf("W99999999", "E88888888")

            // When
            val fetchedNotificationList = notificationRepository.getNotificationSummariesBySourceReference(
                sourceReference,
                VOTER_CARD,
                otherGssCodes,
            )

            // Then
            assertThat(fetchedNotificationList).isEmpty()
        }

        @Test
        fun `should find no notification summaries by source reference and source type given source type that does not exist`() {
            // Given
            val gssCode = aGssCode()
            val sourceReference = aRandomSourceReference()
            notificationRepository.saveNotification(
                aNotificationBuilder(
                    sourceReference = sourceReference,
                    sourceType = VOTER_CARD,
                    gssCode = gssCode,
                ),
            )

            // When
            val fetchedNotificationList = notificationRepository.getNotificationSummariesBySourceReference(
                sourceReference,
                POSTAL,
                listOf(gssCode),
            )

            // Then
            assertThat(fetchedNotificationList).isEmpty()
        }
    }
}
