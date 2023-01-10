package uk.gov.dluhc.notificationsapi.messaging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.messaging.mapper.RemoveNotificationsMapper
import uk.gov.dluhc.notificationsapi.service.RemoveNotificationsService
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRemoveNotificationsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRemoveApplicationNotificationsMessage

@ExtendWith(MockitoExtension::class)
internal class RemoveApplicationNotificationsMessageListenerTest {

    @Mock
    private lateinit var removeNotificationsMapper: RemoveNotificationsMapper

    @Mock
    private lateinit var removeNotificationsService: RemoveNotificationsService

    @InjectMocks
    private lateinit var listener: RemoveApplicationNotificationsMessageListener

    @Test
    fun `should remove application notifications`() {
        // Given
        val sqsMessage = buildRemoveApplicationNotificationsMessage()
        val removeNotificationsDto = buildRemoveNotificationsDto()
        given(removeNotificationsMapper.toRemoveNotificationsDto(any())).willReturn(removeNotificationsDto)

        // When
        listener.handleMessage(sqsMessage)

        // Then
        verify(removeNotificationsMapper).toRemoveNotificationsDto(sqsMessage)
        verify(removeNotificationsService).remove(removeNotificationsDto)
    }
}
