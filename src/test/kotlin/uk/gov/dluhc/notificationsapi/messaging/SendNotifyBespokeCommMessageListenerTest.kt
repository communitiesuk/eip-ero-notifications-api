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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildBespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyBespokeCommMessage

@ExtendWith(MockitoExtension::class)
internal class SendNotifyBespokeCommMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyBespokeCommMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyBespokeCommMessage`() {
        // Given
        val sqsMessage = buildSendNotifyBespokeCommMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildBespokeCommPersonalisationDto()

        given(sendNotifyMessageMapper.fromBespokeCommMessageToSendNotificationRequestDto(sqsMessage)).willReturn(requestDto)
        given(templatePersonalisationMessageMapper.toBespokeCommTemplatePersonalisationDto(sqsMessage.personalisation, requestDto.language, sqsMessage.sourceType, )).willReturn(personalisationDto)
        given(templatePersonalisationDtoMapper.toBespokeCommTemplatePersonalisationMap(personalisationDto, requestDto.language)).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        then(sendNotificationService).should().sendNotification(requestDto, personalisationMap)
    }
}
