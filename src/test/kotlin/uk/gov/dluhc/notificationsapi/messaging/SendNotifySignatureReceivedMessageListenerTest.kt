package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.mapper.SignatureReceivedPersonalisationMapper
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifySignatureReceivedMessage

@ExtendWith(MockitoExtension::class)
class SendNotifySignatureReceivedMessageListenerTest {
    @InjectMocks
    private lateinit var listener: SendNotifySignatureReceivedMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Mock
    private lateinit var signatureReceivedPersonalisationMapper: SignatureReceivedPersonalisationMapper

    @Test
    fun `should handle SQS SendNotifySignatureReceivedMessage`() {
        // Given
        val sqsMessage = buildSendNotifySignatureReceivedMessage()
        val requestDto = aSendNotificationRequestDto()
        val personalisationMap = aNotificationPersonalisationMap()

        given(sendNotifyMessageMapper.fromSignatureReceivedMessageToSendNotificationRequestDto(sqsMessage)).willReturn(requestDto)
        given(
            signatureReceivedPersonalisationMapper.fromMessagePersonalisationToBasePersonalisationMap(
                sqsMessage.personalisation,
                sqsMessage.sourceType,
                sqsMessage.language,
            ),
        ).willReturn(personalisationMap)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(sendNotificationService).sendNotification(requestDto, personalisationMap)
    }
}
