package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.NotificationNotFoundException
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.testsupport.assertj.assertions.entity.NotificationAssert.Companion.assertThat
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntitySourceType
import java.util.UUID

internal class NotificationRepositoryIntegrationTest : IntegrationTest() {

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
            sentAt = sentAt
        )

        // When
        notificationRepository.saveNotification(notification)

        // Then
        val fetchedNotification = notificationRepository.getNotification(id, gssCode)
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
        val gssCode = "E99999999"
        val msg = "Notification item not found for id: 1873fb59-c15d-4473-8ff5-12076b102155 and gssCode: E99999999"

        // When
        val ex = Assertions.catchThrowableOfType(
            { notificationRepository.getNotification(id, gssCode) },
            NotificationNotFoundException::class.java
        )

        // Then
        assertThat(ex).isNotNull
            .isInstanceOf(NotificationNotFoundException::class.java)
            .hasMessage(msg)
    }

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
            sentAt = sentAt
        )
        deleteNotifications(notificationRepository.getBySourceReference(sourceReference, gssCode))
        notificationRepository.saveNotification(notification)

        // When
        val fetchedNotificationList = notificationRepository.getBySourceReference(sourceReference, gssCode)

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
        notificationRepository.saveNotification(aNotification())

        // When
        val fetchedNotificationList = notificationRepository.getBySourceReference(sourceReference, gssCode)

        // Then
        assertThat(fetchedNotificationList).isEmpty()
    }

    @Test
    fun `should remove notifications by source reference`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        notificationRepository.saveNotification(aNotificationBuilder(gssCode = gssCode, sourceReference = sourceReference))

        // When
        notificationRepository.removeBySourceReference(sourceReference, gssCode)

        // Then
        val fetchedNotificationList = notificationRepository.getBySourceReference(sourceReference, gssCode)
        assertThat(fetchedNotificationList).isEmpty()
    }

    @Test
    fun `should get notification by source reference and source type`() {
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
            sentAt = sentAt
        )
        deleteNotifications(notificationRepository.getBySourceReference(sourceReference, gssCode))
        notificationRepository.saveNotification(notification)

        // When
        val fetchedNotificationList = notificationRepository.getBySourceReferenceAndSourceType(sourceReference, gssCode, VOTER_CARD)

        // Then
        assertThat(fetchedNotificationList).hasSize(1)
        val fetchedNotification = fetchedNotificationList[0]
        assertThat(fetchedNotification)
            .hasId(id)
            .hasType(type)
            .hasRequestor(requestor)
            .hasSentAt(sentAt)
            .hasGssCode(null)
            .hasToEmail(null)
            .hasSourceReference(null)
            .hasPersonalisation(null)
            .notifyDetails {
                it.isNull()
            }
    }

    @Test
    fun `should find no notifications by source reference and source type given source reference that does not exist`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        notificationRepository.saveNotification(aNotification())

        // When
        val fetchedNotificationList = notificationRepository.getBySourceReferenceAndSourceType(sourceReference, gssCode, VOTER_CARD)

        // Then
        assertThat(fetchedNotificationList).isEmpty()
    }

    @Test
    fun `should find no notifications by source reference and source type given source type that does not exist`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        notificationRepository.saveNotification(
            aNotificationBuilder(
                sourceReference = sourceReference,
                sourceType = VOTER_CARD
            )
        )

        // When
        val fetchedNotificationList = notificationRepository.getBySourceReferenceAndSourceType(sourceReference, gssCode, POSTAL)

        // Then
        assertThat(fetchedNotificationList).isEmpty()
    }
}
