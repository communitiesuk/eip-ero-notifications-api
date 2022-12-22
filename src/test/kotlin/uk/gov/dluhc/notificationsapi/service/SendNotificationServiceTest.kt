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
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiNotFoundException
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationMapper
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.mapper.PhotoResubmissionPersonalisationMapper
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationPhotoResubmissionRequestDto
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@ExtendWith(MockitoExtension::class)
internal class SendNotificationServiceTest {

    private lateinit var sendNotificationService: SendNotificationService

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var photoResubmissionPersonalisationMapper: PhotoResubmissionPersonalisationMapper

    @Mock
    private lateinit var notifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationMapper: NotificationMapper

    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault())

    @BeforeEach
    fun setUp() {
        sendNotificationService = SendNotificationService(
            notificationRepository,
            notificationTemplateMapper,
            photoResubmissionPersonalisationMapper,
            notifyApiClient,
            notificationMapper,
            fixedClock
        )
    }

    @Test
    fun `should send photo resubmission email notification`() {
        // Given
        val channel = NotificationChannel.EMAIL
        val language = LanguageDto.ENGLISH
        val request = buildSendNotificationPhotoResubmissionRequestDto(channel = channel, language = language)
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = buildPersonalisationMapFromDto()
        val sendNotificationResponseDto = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(sendNotificationResponseDto)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any())).willReturn(templateId)
        given(photoResubmissionPersonalisationMapper.toTemplatePersonalisationMap(any())).willReturn(personalisation)

        // When
        sendNotificationService.sendPhotoResubmissionNotification(request)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(notificationType, channel, language)
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()))
        )
        verify(notificationRepository).saveNotification(notification)
        verify(photoResubmissionPersonalisationMapper).toTemplatePersonalisationMap(request.personalisation)
    }

    @Test
    fun `should send photo resubmission email notification and handle non-retryable Notify client error`() {
        // Given
        val channel = NotificationChannel.EMAIL
        val language = LanguageDto.WELSH
        val request = buildSendNotificationPhotoResubmissionRequestDto(channel = channel, language = language)
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = buildPersonalisationMapFromDto()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willThrow(GovNotifyApiNotFoundException::class.java)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any())).willReturn(templateId)
        given(photoResubmissionPersonalisationMapper.toTemplatePersonalisationMap(any())).willReturn(personalisation)

        // When
        sendNotificationService.sendPhotoResubmissionNotification(request)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(notificationType, channel, language)
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verify(photoResubmissionPersonalisationMapper).toTemplatePersonalisationMap(request.personalisation)
        verifyNoInteractions(notificationMapper, notificationRepository)
    }
}
