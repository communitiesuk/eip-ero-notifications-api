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
import uk.gov.dluhc.notificationsapi.dto.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.mapper.NotificationTypeMapper
import uk.gov.dluhc.notificationsapi.mapper.SourceTypeMapper
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.getIerDsApplicationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoResubmissionPersonalisationMessage
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

    @Test
    fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = PHOTO_RESUBMISSION
        val personalisationMessage = buildPhotoResubmissionPersonalisationMessage()
        val expectedLanguage = LanguageDto.ENGLISH

        given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)
        given(notificationTypeMapper.mapMessageTypeToNotificationType(any())).willReturn(PHOTO_RESUBMISSION)
        given(sourceTypeMapper.toSourceTypeDto(any())).willReturn(expectedSourceType)

        val request = SendNotifyPhotoResubmissionMessage(
            channel = SqsChannel.EMAIL,
            language = Language.EN,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
            applicationId = getIerDsApplicationId(),
            gssCode = gssCode,
            requestor = requestor,
            messageType = MessageType.PHOTO_MINUS_RESUBMISSION,
            toAddress = MessageAddress(emailAddress = emailAddress),
            personalisation = personalisationMessage,
        )

        // When
        val notification = mapper.toSendNotificationRequestDto(request)

        // Then
        assertThat(notification.channel).isEqualTo(expectedChannel)
        assertThat(notification.sourceType).isEqualTo(expectedSourceType)
        assertThat(notification.sourceReference).isEqualTo(sourceReference)
        assertThat(notification.gssCode).isEqualTo(gssCode)
        assertThat(notification.requestor).isEqualTo(requestor)
        assertThat(notification.notificationType).isEqualTo(expectedNotificationType)
        assertThat(notification.emailAddress).isEqualTo(emailAddress)
        verify(languageMapper).fromMessageToDto(Language.EN)
        verify(notificationTypeMapper).mapMessageTypeToNotificationType(MessageType.PHOTO_MINUS_RESUBMISSION)
        verify(sourceTypeMapper).toSourceTypeDto(SqsSourceType.VOTER_MINUS_CARD)
    }
}
