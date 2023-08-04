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
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationAuditMapper
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationMapper
import uk.gov.dluhc.notificationsapi.database.repository.NotificationAuditRepository
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationAudit
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSendNotificationRequestDto
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
    private lateinit var notifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationMapper: NotificationMapper

    @Mock
    private lateinit var notificationAuditRepository: NotificationAuditRepository

    @Mock
    private lateinit var notificationAuditMapper: NotificationAuditMapper

    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(0L), ZoneId.systemDefault())

    @BeforeEach
    fun setUp() {
        sendNotificationService = SendNotificationService(
            notificationRepository,
            notificationTemplateMapper,
            notifyApiClient,
            notificationMapper,
            notificationAuditRepository,
            notificationAuditMapper,
            fixedClock
        )
    }

    @Test
    fun `should send email notification`() {
        // Given
        val channel = NotificationChannel.EMAIL
        val language = LanguageDto.ENGLISH
        val toAddress = NotificationDestinationDto(emailAddress = anEmailAddress(), postalAddress = null)
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val sendNotificationResponseDto = buildSendNotificationDto()
        val notification = aNotification()
        val notificationAudit = aNotificationAudit()
        val templateId = aTemplateId().toString()
        val sourceType = SourceType.VOTER_CARD

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(sendNotificationResponseDto)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)
        given(notificationAuditMapper.createNotificationAudit(any())).willReturn(notificationAudit)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(sourceType, notificationType, channel, language)
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()))
        )
        verify(notificationRepository).saveNotification(notification)
        verify(notificationAuditMapper).createNotificationAudit(notification)
        verify(notificationAuditRepository).saveNotificationAudit(notificationAudit)
    }

    @Test
    fun `should send email notification and handle non-retryable Notify client error`() {
        // Given
        val channel = NotificationChannel.EMAIL
        val language = LanguageDto.WELSH
        val toAddress = NotificationDestinationDto(emailAddress = anEmailAddress(), postalAddress = null)
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val templateId = aTemplateId().toString()
        val sourceType = SourceType.VOTER_CARD

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willThrow(GovNotifyApiNotFoundException::class.java)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(sourceType, notificationType, channel, language)
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verifyNoInteractions(notificationMapper, notificationRepository, notificationAuditMapper, notificationAuditRepository)
    }

    @Test
    fun `should send letter notification`() {
        // Given
        val channel = NotificationChannel.LETTER
        val language = LanguageDto.ENGLISH
        val postalAddress = aPostalAddress()
        val toAddress = NotificationDestinationDto(emailAddress = null, postalAddress = postalAddress)
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val sendNotificationResponseDto = buildSendNotificationDto()
        val notification = aNotification()
        val notificationAudit = aNotificationAudit()
        val templateId = aTemplateId().toString()
        val sourceType = SourceType.VOTER_CARD

        given(notifyApiClient.sendLetter(any(), any(), any(), any())).willReturn(sendNotificationResponseDto)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)
        given(notificationAuditMapper.createNotificationAudit(any())).willReturn(notificationAudit)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(sourceType, notificationType, channel, language)
        verify(notifyApiClient).sendLetter(eq(templateId), eq(postalAddress), eq(personalisation), any())
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault()))
        )
        verify(notificationRepository).saveNotification(notification)
        verify(notificationAuditMapper).createNotificationAudit(notification)
        verify(notificationAuditRepository).saveNotificationAudit(notificationAudit)
    }

    @Test
    fun `should send letter notification and handle non-retryable Notify client error`() {
        // Given
        val channel = NotificationChannel.LETTER
        val language = LanguageDto.WELSH
        val postalAddress = aPostalAddress()
        val toAddress = NotificationDestinationDto(emailAddress = null, postalAddress = postalAddress)
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val templateId = aTemplateId().toString()
        val sourceType = SourceType.VOTER_CARD

        given(notifyApiClient.sendLetter(any(), any(), any(), any())).willThrow(GovNotifyApiNotFoundException::class.java)
        given(notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(any(), any(), any(), any())).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(sourceType, notificationType, channel, language)
        verify(notifyApiClient).sendLetter(eq(templateId), eq(postalAddress), eq(personalisation), any())
        verifyNoInteractions(notificationMapper, notificationRepository, notificationAuditMapper, notificationAuditRepository)
    }
}
