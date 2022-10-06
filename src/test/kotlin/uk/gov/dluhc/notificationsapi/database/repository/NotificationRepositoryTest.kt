package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotifyDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntityNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEntitySourceType

internal class NotificationRepositoryTest : IntegrationTest() {

    @Test
    fun `should save and retrieve email notification`() {
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
        assertThat(fetchedNotification.id).isEqualTo(id)
        assertThat(fetchedNotification.gssCode).isEqualTo(gssCode)
        assertThat(fetchedNotification.type).isEqualTo(type)
        assertThat(fetchedNotification.toEmail).isEqualTo(toEmail)
        assertThat(fetchedNotification.requestor).isEqualTo(requestor)
        assertThat(fetchedNotification.sourceReference).isEqualTo(sourceReference)
        assertThat(fetchedNotification.personalisation).isEqualTo(personalisation)
        assertThat(fetchedNotification.notifyDetails).isEqualTo(notifyDetails)
        assertThat(fetchedNotification.sentAt).isEqualTo(sentAt)
    }
}
