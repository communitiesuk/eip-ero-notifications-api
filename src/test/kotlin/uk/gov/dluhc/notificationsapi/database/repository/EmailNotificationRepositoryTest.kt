package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.EmailNotification
import java.util.UUID

internal class EmailNotificationRepositoryTest : IntegrationTest() {

    @Test
    fun `should save and retrieve email notification`() {
        // Given
        val notificationId = UUID.randomUUID().toString()
        val emailNotification = EmailNotification(notificationId)
// notification id (aka PK if it were RDBMS)
// application reference (agnostic to vca/overseas/proxy/postal etc)
// “source system” type (I know @Matt Wills hates this term :smile: )
// ero id
// template type
// template id
// template version (comes back in field templateVersion in SendEmailResponse)
// email body that was sent
// subject of sent email
// from addr of sent email
// to addr of sent email
// date-time stamp of send
// requestor (who initiated the notification to be sent)
// map of data that was originally sent for the notification

        // When
        emailNotificationRepository.saveEmailNotification(emailNotification)

        // Then
        val saveEmailNotification = emailNotificationRepository.getEmailNotification(notificationId)
        assertThat(saveEmailNotification.id).isEqualTo(notificationId)
    }
}