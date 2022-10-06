package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.messaging.mapper.SendNotifyMessageMapper
import uk.gov.dluhc.notificationsapi.service.SendNotificationService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.aSendNotifyMessage

@ExtendWith(MockitoExtension::class)
internal class SendNotifyMessageListenerTest {

    @InjectMocks
    private lateinit var listener: SendNotifyMessageListener

    @Mock
    private lateinit var sendNotifyMessageMapper: SendNotifyMessageMapper

    @Mock
    private lateinit var sendNotificationService: SendNotificationService

    @Test
    fun `should handle SQS SendNotifyMessage`() {
        // Given
        val sqsMessage = aSendNotifyMessage()
        val request = aSendNotificationRequestDto()
        given(sendNotifyMessageMapper.toSendNotificationRequestDto(any())).willReturn(request)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(sendNotifyMessageMapper).toSendNotificationRequestDto(sqsMessage)
        verify(sendNotificationService).sendNotification(request)
    }
}
