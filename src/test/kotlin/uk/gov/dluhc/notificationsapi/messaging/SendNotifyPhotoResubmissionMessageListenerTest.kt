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
import uk.gov.dluhc.notificationsapi.messaging.mapper.PhotoResubmissionPersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
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
    private lateinit var photoResubmissionPersonalisationMessageMapper: PhotoResubmissionPersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyMessage`() {
        // Given
        val sqsMessage = aSendNotifyPhotoResubmissionMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val photoResubmissionPersonalisationDto = buildPhotoPersonalisationDto()

        given(sendNotifyMessageMapper.toSendNotificationRequestDto(any())).willReturn(requestDto)
        given(photoResubmissionPersonalisationMessageMapper.toPhotoResubmissionPersonalisationDto(any())).willReturn(photoResubmissionPersonalisationDto)
        given(templatePersonalisationDtoMapper.toPhotoResubmissionTemplatePersonalisationMap(any())).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(sendNotifyMessageMapper).toSendNotificationRequestDto(sqsMessage)
        verify(photoResubmissionPersonalisationMessageMapper).toPhotoResubmissionPersonalisationDto(sqsMessage.personalisation)
        verify(templatePersonalisationDtoMapper).toPhotoResubmissionTemplatePersonalisationMap(photoResubmissionPersonalisationDto)
        verify(sendNotificationService).sendNotification(requestDto, personalisationMap)
    }
}
