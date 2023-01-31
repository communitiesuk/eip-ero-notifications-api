package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyApplicationApprovedMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyIdDocumentResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotificationDestination
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.aMessageAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildApplicationApprovedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildIdDocumentPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoPersonalisationMessage
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel as SqsChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: SendNotifyMessageMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationDestinationDtoMapper: NotificationDestinationDtoMapper

    @Test
    fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val toAddress = aMessageAddress()
        val expectedToAddress = aNotificationDestination()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = PHOTO_RESUBMISSION
        val personalisationMessage = buildPhotoPersonalisationMessage()
        val expectedLanguage = LanguageDto.ENGLISH

        given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
        given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
        given(sourceTypeMapper.toSourceTypeDto(any())).willReturn(expectedSourceType)
        given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

        val request = SendNotifyPhotoResubmissionMessage(
            channel = SqsChannel.EMAIL,
            language = Language.EN,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
            gssCode = gssCode,
            requestor = requestor,
            messageType = MessageType.PHOTO_MINUS_RESUBMISSION,
            toAddress = toAddress,
            personalisation = personalisationMessage,
        )

        // When
        val notification = mapper.fromPhotoMessageToSendNotificationRequestDto(request)

        // Then
        assertThat(notification.channel).isEqualTo(expectedChannel)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        assertThat(notification.toAddress).isEqualTo(expectedToAddress)
        verify(languageMapper).fromMessageToDto(Language.EN)
        verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.PHOTO_MINUS_RESUBMISSION)
        verify(sourceTypeMapper).toSourceTypeDto(SqsSourceType.VOTER_MINUS_CARD)
        verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
    }

    @Test
    fun `should map SQS SendNotifyIdDocumentResubmissionMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val toAddress = aMessageAddress()
        val expectedToAddress = aNotificationDestination()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
        val personalisationMessage = buildIdDocumentPersonalisationMessage()
        val expectedLanguage = LanguageDto.ENGLISH

        given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
        given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
        given(sourceTypeMapper.toSourceTypeDto(any())).willReturn(expectedSourceType)
        given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

        val request = SendNotifyIdDocumentResubmissionMessage(
            channel = SqsChannel.EMAIL,
            language = Language.EN,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
            gssCode = gssCode,
            requestor = requestor,
            messageType = MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION,
            toAddress = toAddress,
            personalisation = personalisationMessage,
        )

        // When
        val notification = mapper.fromIdDocumentMessageToSendNotificationRequestDto(request)

        // Then
        assertThat(notification.channel).isEqualTo(expectedChannel)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        assertThat(notification.toAddress).isEqualTo(expectedToAddress)
        verify(languageMapper).fromMessageToDto(Language.EN)
        verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.ID_MINUS_DOCUMENT_MINUS_RESUBMISSION)
        verify(sourceTypeMapper).toSourceTypeDto(SqsSourceType.VOTER_MINUS_CARD)
        verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
    }

    @Test
    fun `should map SQS SendNotifyApplicationApprovedMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val toAddress = aMessageAddress()
        val expectedToAddress = aNotificationDestination()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = ID_DOCUMENT_RESUBMISSION
        val personalisation = buildApplicationApprovedPersonalisation()
        val expectedLanguage = LanguageDto.ENGLISH

        given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
        given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(expectedNotificationType)
        given(sourceTypeMapper.toSourceTypeDto(any())).willReturn(expectedSourceType)
        given(notificationDestinationDtoMapper.toNotificationDestinationDto(any())).willReturn(expectedToAddress)

        val request = SendNotifyApplicationApprovedMessage(
            language = Language.EN,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
            gssCode = gssCode,
            requestor = requestor,
            messageType = MessageType.APPLICATION_MINUS_APPROVED,
            toAddress = toAddress,
            personalisation = personalisation,
        )

        // When
        val notification = mapper.fromApprovedMessageToSendNotificationRequestDto(request)

        // Then
        assertThat(notification.channel).isEqualTo(expectedChannel)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        assertThat(notification.toAddress).isEqualTo(expectedToAddress)
        verify(languageMapper).fromMessageToDto(Language.EN)
        verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.APPLICATION_MINUS_APPROVED)
        verify(sourceTypeMapper).toSourceTypeDto(SqsSourceType.VOTER_MINUS_CARD)
        verify(notificationDestinationDtoMapper).toNotificationDestinationDto(toAddress)
    }
}
