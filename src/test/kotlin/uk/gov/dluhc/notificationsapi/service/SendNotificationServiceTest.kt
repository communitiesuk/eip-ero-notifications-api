package uk.gov.dluhc.notificationsapi.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anOverseasAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotification
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationAudit
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotificationDestination
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequiredDocumentPersonalisationMapFromDto
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

    @Mock
    private lateinit var statisticsUpdateService: StatisticsUpdateService

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
            statisticsUpdateService,
            fixedClock,
        )
    }

    @Test
    fun `should send email notification`() {
        // Given
        val channel = CommunicationChannel.EMAIL
        val language = LanguageDto.ENGLISH
        val toAddress =
            NotificationDestinationDto(
                emailAddress = anEmailAddress(),
                postalAddress = null,
                overseasElectorAddress = null,
            )
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
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)
        given(notificationAuditMapper.createNotificationAudit(any())).willReturn(notificationAudit)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
            sourceType,
            notificationType,
            channel,
            language,
        )
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault())),
        )
        verify(notificationRepository).saveNotification(notification)
        verify(notificationAuditMapper).createNotificationAudit(notification)
        verify(notificationAuditRepository).saveNotificationAudit(notificationAudit)
    }

    @Test
    fun `should send email notification and handle non-retryable Notify client error`() {
        // Given
        val channel = CommunicationChannel.EMAIL
        val language = LanguageDto.WELSH
        val toAddress =
            NotificationDestinationDto(
                emailAddress = anEmailAddress(),
                postalAddress = null,
                overseasElectorAddress = null,
            )
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val emailAddress = anEmailAddress()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val templateId = aTemplateId().toString()
        val sourceType = SourceType.VOTER_CARD

        given(
            notifyApiClient.sendEmail(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willThrow(GovNotifyApiNotFoundException::class.java)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
            sourceType,
            notificationType,
            channel,
            language,
        )
        verify(notifyApiClient).sendEmail(eq(templateId), eq(emailAddress), eq(personalisation), any())
        verifyNoInteractions(
            notificationMapper,
            notificationRepository,
            notificationAuditMapper,
            notificationAuditRepository,
        )
    }

    @Test
    fun `should send letter notification`() {
        // Given
        val channel = CommunicationChannel.LETTER
        val language = LanguageDto.ENGLISH
        val postalAddress = aPostalAddress()
        val toAddress =
            NotificationDestinationDto(
                emailAddress = null,
                postalAddress = postalAddress,
                overseasElectorAddress = null,
            )
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val sendNotificationResponseDto = buildSendNotificationDto()
        val notification = aNotification()
        val notificationAudit = aNotificationAudit()
        val templateId = aTemplateId().toString()
        val notificationDestinationDto =
            aNotificationDestination(emailAddress = null, overseasAddress = null, postalAddress = postalAddress)
        val sourceType = SourceType.VOTER_CARD

        given(notifyApiClient.sendLetter(any(), any(), any(), any(), any())).willReturn(sendNotificationResponseDto)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)
        given(notificationAuditMapper.createNotificationAudit(any())).willReturn(notificationAudit)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
            sourceType,
            notificationType,
            channel,
            language,
        )
        verify(notifyApiClient).sendLetter(
            eq(templateId),
            eq(notificationDestinationDto),
            eq(personalisation),
            any(),
            eq(sourceType),
        )
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault())),
        )
        verify(notificationRepository).saveNotification(notification)
        verify(notificationAuditMapper).createNotificationAudit(notification)
        verify(notificationAuditRepository).saveNotificationAudit(notificationAudit)
    }

    @ParameterizedTest
    @EnumSource(
        NotificationType::class,
        names = [
            "NINO_NOT_MATCHED",
            "PARENT_GUARDIAN_PROOF_REQUIRED",
            "PREVIOUS_ADDRESS_DOCUMENT_REQUIRED",
            "REJECTED_DOCUMENT",
            "REJECTED_PARENT_GUARDIAN",
            "REJECTED_PREVIOUS_ADDRESS",
            "BESPOKE_COMM",
        ],
    )
    fun `should send letter notification for overseas address`(notificationType: NotificationType) {
        // Given
        val channel = CommunicationChannel.LETTER
        val language = LanguageDto.ENGLISH
        val overseasAddress = anOverseasAddress()
        val toAddress =
            NotificationDestinationDto(
                emailAddress = null,
                postalAddress = null,
                overseasElectorAddress = overseasAddress,
            )
        val sourceType = SourceType.OVERSEAS
        val request = buildSendNotificationRequestDto(
            sourceType = sourceType,
            channel = channel,
            language = language,
            toAddress = toAddress,
            notificationType = notificationType,
        )
        val personalisation =
            buildRequiredDocumentPersonalisationMapFromDto(buildRequiredDocumentPersonalisation(), sourceType)
        val sendNotificationResponseDto = buildSendNotificationDto()
        val notification = aNotification()
        val notificationAudit = aNotificationAudit()
        val templateId = aTemplateId().toString()
        val notificationDestinationDto =
            aNotificationDestination(emailAddress = null, overseasAddress = overseasAddress, postalAddress = null)

        given(notifyApiClient.sendLetter(any(), any(), any(), any(), any())).willReturn(sendNotificationResponseDto)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)
        given(notificationAuditMapper.createNotificationAudit(any())).willReturn(notificationAudit)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
            sourceType,
            notificationType,
            channel,
            language,
        )
        verify(notifyApiClient).sendLetter(
            eq(templateId),
            eq(notificationDestinationDto),
            eq(personalisation),
            any(),
            eq(sourceType),
        )
        verify(notificationMapper).createNotification(
            any(),
            eq(request),
            eq(personalisation),
            eq(sendNotificationResponseDto),
            eq(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault())),
        )
        verify(notificationRepository).saveNotification(notification)
        verify(notificationAuditMapper).createNotificationAudit(notification)
        verify(notificationAuditRepository).saveNotificationAudit(notificationAudit)
    }

    @Test
    fun `should send letter notification and handle non-retryable Notify client error`() {
        // Given
        val channel = CommunicationChannel.LETTER
        val language = LanguageDto.WELSH
        val postalAddress = aPostalAddress()
        val toAddress =
            NotificationDestinationDto(
                emailAddress = null,
                postalAddress = postalAddress,
                overseasElectorAddress = null,
            )
        val request = buildSendNotificationRequestDto(channel = channel, language = language, toAddress = toAddress)
        val notificationType = aNotificationType()
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val templateId = aTemplateId().toString()
        val notificationDestinationDto =
            aNotificationDestination(emailAddress = null, overseasAddress = null, postalAddress = postalAddress)
        val sourceType = SourceType.VOTER_CARD

        given(
            notifyApiClient.sendLetter(
                any(),
                any(),
                any(),
                any(),
                any(),
            ),
        ).willThrow(GovNotifyApiNotFoundException::class.java)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(notificationTemplateMapper).fromNotificationTypeForChannelInLanguage(
            sourceType,
            notificationType,
            channel,
            language,
        )
        verify(notifyApiClient).sendLetter(
            eq(templateId),
            eq(notificationDestinationDto),
            eq(personalisation),
            any(),
            eq(sourceType),
        )
        verifyNoInteractions(
            notificationMapper,
            notificationRepository,
            notificationAuditMapper,
            notificationAuditRepository,
        )
    }

    @ParameterizedTest
    @EnumSource(
        NotificationType::class,
        names = ["PHOTO_RESUBMISSION", "PHOTO_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_RESUBMISSION", "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_REQUIRED", "BESPOKE_COMM", "NOT_REGISTERED_TO_VOTE"],
    )
    fun `should send votercard statistics update for relevant notification types`(notificationType: NotificationType) {
        // Given
        val request = buildSendNotificationRequestDto(notificationType = notificationType)
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(statisticsUpdateService).triggerStatisticsUpdate(notification.sourceReference!!)
    }

    @ParameterizedTest
    @EnumSource(
        NotificationType::class,
        names = ["PHOTO_RESUBMISSION", "PHOTO_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_RESUBMISSION", "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_REQUIRED", "BESPOKE_COMM", "NOT_REGISTERED_TO_VOTE"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun `should not send votercard statistics update for irrelevant notification types`(notificationType: NotificationType) {
        // Given
        val request = buildSendNotificationRequestDto(notificationType = notificationType)
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verifyNoInteractions(statisticsUpdateService)
    }

    @ParameterizedTest
    @EnumSource(
        NotificationType::class,
        names = ["ID_DOCUMENT_RESUBMISSION", "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_REQUIRED", "REQUESTED_SIGNATURE", "REJECTED_SIGNATURE", "NINO_NOT_MATCHED", "REJECTED_SIGNATURE_WITH_REASONS", "BESPOKE_COMM", "NOT_REGISTERED_TO_VOTE", "SIGNATURE_RESUBMISSION", "SIGNATURE_RESUBMISSION_WITH_REASONS"],
    )
    fun `should send postal statistics update for relevant notification types`(notificationType: NotificationType) {
        // Given
        val request =
            buildSendNotificationRequestDto(notificationType = notificationType, sourceType = SourceType.POSTAL)
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(statisticsUpdateService).triggerStatisticsUpdate(notification.sourceReference!!)
    }

    @ParameterizedTest
    @EnumSource(
        NotificationType::class,
        names = ["ID_DOCUMENT_RESUBMISSION", "ID_DOCUMENT_RESUBMISSION_WITH_REASONS", "ID_DOCUMENT_REQUIRED", "REQUESTED_SIGNATURE", "REJECTED_SIGNATURE", "NINO_NOT_MATCHED", "REJECTED_SIGNATURE_WITH_REASONS", "BESPOKE_COMM", "NOT_REGISTERED_TO_VOTE", "SIGNATURE_RESUBMISSION", "SIGNATURE_RESUBMISSION_WITH_REASONS"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun `should not send postal statistics update for irrelevant notification types`(notificationType: NotificationType) {
        // Given
        val request =
            buildSendNotificationRequestDto(notificationType = notificationType, sourceType = SourceType.POSTAL)
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verifyNoInteractions(statisticsUpdateService)
    }

    @ParameterizedTest
    @EnumSource(SourceType::class, names = ["VOTER_CARD", "POSTAL", "PROXY", "OVERSEAS"])
    fun `should send statistics update for Proxy, Postal and Voter Card applications`(sourceType: SourceType) {
        // Given
        val request = buildSendNotificationRequestDto(
            sourceType = sourceType,
            notificationType = NotificationType.ID_DOCUMENT_REQUIRED,
        )
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verify(statisticsUpdateService).triggerStatisticsUpdate(notification.sourceReference!!)
    }

    @ParameterizedTest
    @EnumSource(
        SourceType::class,
        names = ["VOTER_CARD", "POSTAL", "PROXY", "OVERSEAS"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun `should not send statistics update for irrelevant source types`(sourceType: SourceType) {
        // Given
        val request = buildSendNotificationRequestDto(
            sourceType = sourceType,
            notificationType = NotificationType.ID_DOCUMENT_REQUIRED,
        )
        val personalisation = buildPhotoPersonalisationMapFromDto()
        val response = buildSendNotificationDto()
        val notification = aNotification()
        val templateId = aTemplateId().toString()

        given(notifyApiClient.sendEmail(any(), any(), any(), any())).willReturn(response)
        given(notificationMapper.createNotification(any(), any(), any(), any(), any())).willReturn(notification)
        given(
            notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                any(),
                any(),
                any(),
                any(),
            ),
        ).willReturn(templateId)

        // When
        sendNotificationService.sendNotification(request, personalisation)

        // Then
        verifyNoInteractions(statisticsUpdateService)
    }
}
