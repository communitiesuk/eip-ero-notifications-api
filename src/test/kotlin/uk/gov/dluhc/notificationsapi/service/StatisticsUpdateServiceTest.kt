package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.messagingsupport.MessageQueue
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage

@ExtendWith(MockitoExtension::class)
class StatisticsUpdateServiceTest {

    @InjectMocks
    private lateinit var statisticsUpdateService: StatisticsUpdateService

    @Mock
    private lateinit var triggerUpdateStatisticsMessageQueue: MessageQueue<UpdateStatisticsMessage>

    @Captor
    private lateinit var headersArgumentCaptor: ArgumentCaptor<Map<String, Any>>

    @Test
    fun `should send a message containing the application ID to the queue`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD)

        // Then
        verify(triggerUpdateStatisticsMessageQueue).submit(
            eq(UpdateStatisticsMessage(applicationId)),
            any()
        )
        verifyNoMoreInteractions(triggerUpdateStatisticsMessageQueue)
    }

    @Test
    fun `should include message-group-id and message-deduplication-id headers on the message`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD)

        // Then
        verify(triggerUpdateStatisticsMessageQueue).submit(
            any(),
            capture(headersArgumentCaptor)
        )
        assertThat(headersArgumentCaptor.value.get("message-group-id")).isEqualTo(applicationId)
        assertThat(headersArgumentCaptor.value.get("message-deduplication-id")).isNotNull
        verifyNoMoreInteractions(triggerUpdateStatisticsMessageQueue)
    }
}
