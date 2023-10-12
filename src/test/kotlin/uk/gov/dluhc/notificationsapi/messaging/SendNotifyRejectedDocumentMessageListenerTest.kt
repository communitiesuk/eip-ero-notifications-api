package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedDocumentMessage

@ExtendWith(MockitoExtension::class)
internal class SendNotifyRejectedDocumentMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyRejectedDocumentMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyRejectedDocumentMessage`() {
        // Given
        val sqsMessage = buildSendNotifyRejectedDocumentMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildRejectedDocumentPersonalisationDto()

        given(sendNotifyMessageMapper.fromRejectedDocumentMessageToSendNotificationRequestDto(sqsMessage)).willReturn(requestDto)
        given(templatePersonalisationMessageMapper.toRejectedDocumentPersonalisationDto(sqsMessage.personalisation, requestDto.language, sqsMessage.sourceType)).willReturn(personalisationDto)
        given(templatePersonalisationDtoMapper.toRejectedDocumentTemplatePersonalisationMap(personalisationDto)).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        then(sendNotificationService).should().sendNotification(requestDto, personalisationMap)
    }
}
