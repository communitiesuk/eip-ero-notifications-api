package uk.gov.dluhc.notificationsapi.database.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.domain.EmailNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationDto

internal class EmailNotificationRepositoryTest : IntegrationTest() {

    @Test
    fun `should save and retrieve email notification`() {
        // Given
        val id = aNotificationId()
        val gssCode = aGssCode()
        val type = aNotificationType()
        val toEmail = anEmailAddress()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val sourceType = aSourceType()
        val personalisation = aNotificationPersonalisationMap()
        val notifyDetails = aSendNotificationDto()
        val sentAt = aLocalDateTime()
        val emailNotification = EmailNotification(
            id = id,
            gssCode = gssCode,
            type = type,
            toEmail = toEmail,
            requestor = requestor,
            sourceReference = sourceReference,
            sourceType = sourceType,
            personalisation = personalisation,
            notifyDetails = notifyDetails,
            sentAt = sentAt
        )

        // When
        emailNotificationRepository.saveEmailNotification(emailNotification)

        // Then
        val saveEmailNotification = emailNotificationRepository.getEmailNotification(id, gssCode)
        assertThat(saveEmailNotification.id).isEqualTo(id)
        assertThat(saveEmailNotification.gssCode).isEqualTo(gssCode)
        assertThat(saveEmailNotification.type).isEqualTo(type)
        assertThat(saveEmailNotification.toEmail).isEqualTo(toEmail)
        assertThat(saveEmailNotification.requestor).isEqualTo(requestor)
        assertThat(saveEmailNotification.sourceReference).isEqualTo(sourceReference)
        assertThat(saveEmailNotification.personalisation).isEqualTo(personalisation)
        assertThat(saveEmailNotification.notifyDetails).isEqualTo(notifyDetails)
        assertThat(saveEmailNotification.sentAt).isEqualTo(sentAt)
    }
}
