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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyNinoNotMatchedMessage

@ExtendWith(MockitoExtension::class)
internal class SendNotifyNinoNotMatchedMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyNinoNotMatchedMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyNinoNotMatchedMessage`() {
        // Given
        val sqsMessage = buildSendNotifyNinoNotMatchedMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildNinoNotMatchedPersonalisationDto()

        given(
            sendNotifyMessageMapper.fromRequiredDocumentMessageToSendNotificationRequestDto(
                sqsMessage,
            )
        ).willReturn(
            requestDto
        )
        given(
            templatePersonalisationMessageMapper.toRequiredDocumentTemplatePersonalisationDto(
                sqsMessage.personalisation,
                requestDto.language,
                sqsMessage.sourceType
            )
        ).willReturn(personalisationDto)
        given(
            templatePersonalisationDtoMapper.toRequiredDocumentTemplatePersonalisationMap(
                personalisationDto,
                requestDto.sourceType
            )
        ).willReturn(
            personalisationMap
        )

        // When
        listener.handleMessage(sqsMessage)

        // Then
        then(sendNotificationService).should().sendNotification(requestDto, personalisationMap)
    }
}
