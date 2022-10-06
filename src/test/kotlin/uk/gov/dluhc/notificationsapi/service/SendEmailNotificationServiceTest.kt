package uk.gov.dluhc.notificationsapi.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.repository.EmailNotificationRepository
import uk.gov.dluhc.notificationsapi.factory.EmailNotificationFactory
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.anEmailNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildSendNotificationDto
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
internal class SendEmailNotificationServiceTest {

    private lateinit var sendEmailNotificationService: SendEmailNotificationService

    @Mock
    private lateinit var emailNotificationRepository: EmailNotificationRepository

    @Mock
    private lateinit var notifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var emailNotificationFactory: EmailNotificationFactory

    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault())

    @BeforeEach
    fun setUp() {
        sendEmailNotificationService = SendEmailNotificationService(
            emailNotificationRepository,
            notifyApiClient,
            emailNotificationFactory,
            fixedClock
        )
    }

    @Test
    fun `should send email notification`() {
        // Given
        val request = aSendNotificationRequestDto()
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = aNotificationPersonalisationMap()
        val sendNotificationDto = buildSendNotificationDto()
        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(sendNotificationDto)
        val notification = anEmailNotification()
        given(emailNotificationFactory.createEmailNotification(any(), any(), any(), any())).willReturn(notification)

        // When
        sendEmailNotificationService.sendEmailNotification(request)

        // Then
        verify(notifyApiClient).sendEmail(eq(notificationType), eq(emailAddress), eq(personalisation), any())
        verify(emailNotificationFactory).createEmailNotification(
            any(),
            eq(request),
            eq(sendNotificationDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()))
        )
        verify(emailNotificationRepository).saveEmailNotification(notification)
    }
}
