package uk.gov.dluhc.notificationsapi.factory

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildSendNotificationRequestDto

internal class EmailNotificationFactoryTest {

    private val factory = EmailNotificationFactory()

    @Test
    fun `should create email notification`() {
        // Given
        val notificationId = aNotificationId()
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceType = aSourceType()
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val notificationType = aNotificationType()
        val personalisation = aNotificationPersonalisationMap()
        val request = buildSendNotificationRequestDto(
            gssCode = gssCode,
            requestor = requestor,
            sourceType = sourceType,
            sourceReference = sourceReference,
            emailAddress = emailAddress,
            notificationType = notificationType,
            personalisation = personalisation,
        )
        val sendNotificationDto = aSendNotificationDto()
        val sentAt = aLocalDateTime()

        // When
        val notification = factory.createEmailNotification(notificationId, request, sendNotificationDto, sentAt)

        // Then
        assertThat(notification.id).isEqualTo(notificationId)
        assertThat(notification.type).isEqualTo(notificationType)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.sourceType).isEqualTo(sourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.toEmail).isEqualTo(emailAddress)
        assertThat(notification.personalisation).isEqualTo(personalisation)
        assertThat(notification.notifyDetails).isEqualTo(sendNotificationDto)
        assertThat(notification.sentAt).isEqualTo(sentAt)
    }
}
