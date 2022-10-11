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
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationMapper
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildSendNotificationDto
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
internal class SendNotificationServiceTestDto {

    private lateinit var sendNotificationService: SendNotificationService

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var notifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationMapper: NotificationMapper

    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault())

    @BeforeEach
    fun setUp() {
        sendNotificationService = SendNotificationService(
            notificationRepository,
            notifyApiClient,
            notificationMapper,
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
        val notification = aNotification()
        given(notificationMapper.createNotification(any(), any(), any(), any())).willReturn(notification)

        // When
        sendNotificationService.sendNotification(request)

        // Then
        verify(notifyApiClient).sendEmail(eq(notificationType), eq(emailAddress), eq(personalisation), any())
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(sendNotificationDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()))
        )
        verify(notificationRepository).saveNotification(notification)
    }
}
