package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.mapper.TemplatePersonalisationDtoMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.TemplatePersonalisationMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifySignatureResubmissionMessage

@ExtendWith(MockitoExtension::class)
class SendNotifySignatureResubmissionMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifySignatureResubmissionMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var templatePersonalisationMessageMapper: TemplatePersonalisationMessageMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifySignatureResubmissionMessage`() {
        // Given
        val sqsMessage = buildSendNotifySignatureResubmissionMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()
        val personalisationDto = buildSignatureResubmissionPersonalisationDto()

        given(sendNotifyMessageMapper.fromSignatureResubmissionMessageToSendNotificationRequestDto(sqsMessage)).willReturn(requestDto)
        given(
            templatePersonalisationMessageMapper.toSignatureResubmissionTemplatePersonalisationDto(
                sqsMessage.personalisation,
                sqsMessage.language,
                sqsMessage.sourceType,
            ),
        ).willReturn(personalisationDto)
        given(
            templatePersonalisationDtoMapper.toSignatureResubmissionPersonalisation(
                personalisationDto,
            ),
        ).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(sendNotificationService).sendNotification(requestDto, personalisationMap)
    }
}
