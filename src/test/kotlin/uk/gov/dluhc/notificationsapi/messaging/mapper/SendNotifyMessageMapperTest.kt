package uk.gov.dluhc.notificationsapi.messaging.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.mapper.LanguageMapper
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.MessageAddress
import uk.gov.dluhc.notificationsapi.messaging.models.MessageType
import uk.gov.dluhc.notificationsapi.messaging.models.SendNotifyPhotoResubmissionMessage
import uk.gov.dluhc.notificationsapi.messaging.models.TemplatePersonalisationNameValue
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDtoFromMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildPhotoResubmissionPersonalisationMessage
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel as SqsChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SqsSourceType

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageMapperTest {

    @InjectMocks
    private lateinit var mapper: SendNotifyMessageMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_MINUS_CARD, VOTER_CARD",
        ]
    )
    fun `should map Source Type`(sourceType: SqsSourceType, expected: SourceType) {
        // Given

        // When
        val actual = mapper.mapToSourceType(sourceType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_RECEIVED, APPLICATION_RECEIVED",
            "APPLICATION_MINUS_APPROVED, APPLICATION_APPROVED",
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_RESUBMISSION, PHOTO_RESUBMISSION",
        ]
    )
    fun `should map Message Type to NotificationType `(messageType: MessageType, expected: NotificationType) {
        // Given

        // When
        val actual = mapper.mapToNotificationType(messageType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should create email notification`() {
        // Given
        val placeholders = listOf(
            TemplatePersonalisationNameValue(name = "subject", value = "test subject"),
            TemplatePersonalisationNameValue(name = "applicant_name", value = "John"),
            TemplatePersonalisationNameValue(name = "custom_title", value = "Resubmitting photo"),
            TemplatePersonalisationNameValue(name = "date", value = "15/Oct/2022")
        )
        val expected = mapOf(
            "subject" to "test subject",
            "applicant_name" to "John",
            "custom_title" to "Resubmitting photo",
            "date" to "15/Oct/2022"
        )

        // When
        val actual = mapper.map(placeholders)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should map SQS SendNotifyPhotoResubmissionMessage to SendNotificationRequestDto`() {
        // Given
        val gssCode = aGssCode()
        val requestor = aRequestor()
        val sourceReference = aSourceReference()
        val emailAddress = anEmailAddress()
        val expectedChannel = NotificationChannel.EMAIL
        val expectedSourceType = SourceType.VOTER_CARD
        val expectedNotificationType = NotificationType.PHOTO_RESUBMISSION
        val personalisationMessage = buildPhotoResubmissionPersonalisationMessage()
        val expectedPersonalisationDto = buildPhotoResubmissionPersonalisationDtoFromMessage(personalisationMessage)
        val expectedLanguage = LanguageDto.ENGLISH

        given(languageMapper.fromMessageToDto(any())).willReturn(expectedLanguage)

        val request = SendNotifyPhotoResubmissionMessage(
            channel = SqsChannel.EMAIL,
            language = Language.EN,
            sourceType = SqsSourceType.VOTER_MINUS_CARD,
            sourceReference = sourceReference,
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
        assertThat(notification.personalisation).isEqualTo(expectedPersonalisationDto)
        assertThat(notification.emailAddress).isEqualTo(emailAddress)
        verify(languageMapper).fromMessageToDto(Language.EN)
    }
}
