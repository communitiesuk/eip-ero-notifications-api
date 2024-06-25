package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedSignatureMessage

@ExtendWith(MockitoExtension::class)
class SendNotifyRejectedSignatureMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyRejectedSignatureMessageListener

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
        val sqsMessage = buildSendNotifyRejectedSignatureMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildRejectedSignaturePersonalisationDto()

        BDDMockito.given(sendNotifyMessageMapper.fromRejectedSignatureToSendNotificationRequestDto(sqsMessage)).willReturn(requestDto)
        BDDMockito.given(
            templatePersonalisationMessageMapper.toRejectedSignaturePersonalisationDto(
                sqsMessage.personalisation,
                requestDto.language,
                sqsMessage.sourceType,
            ),
        ).willReturn(personalisationDto)
        BDDMockito.given(
            templatePersonalisationDtoMapper.toRejectedSignatureTemplatePersonalisationMap(
                personalisationDto,
            ),
        ).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        BDDMockito.then(sendNotificationService).should().sendNotification(requestDto, personalisationMap)
    }
}
