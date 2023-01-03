package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.aSendNotifyPhotoResubmissionMessage

@ExtendWith(MockitoExtension::class)
internal class SendNotifyPhotoResubmissionMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyPhotoResubmissionMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyPhotoResubmissionMessage`() {
        // Given
        val sqsMessage = aSendNotifyPhotoResubmissionMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildPhotoPersonalisationDto()

        given(sendNotifyMessageMapper.fromPhotoMessageToSendNotificationRequestDto(any())).willReturn(requestDto)
        given(templatePersonalisationMessageMapper.toPhotoPersonalisationDto(any())).willReturn(personalisationDto)
        given(templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(any())).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(sendNotifyMessageMapper).fromPhotoMessageToSendNotificationRequestDto(sqsMessage)
        verify(templatePersonalisationMessageMapper).toPhotoPersonalisationDto(sqsMessage.personalisation)
        verify(templatePersonalisationDtoMapper).toPhotoResubmissionTemplatePersonalisationMap(personalisationDto)
        verify(sendNotificationService).sendNotification(requestDto, personalisationMap)
    }
}
