package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
import org.mockito.kotlin.verifyNoInteractions
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

    @Mock
    private lateinit var triggerVoterCardStatisticsUpdateQueue: MessageQueue<UpdateStatisticsMessage>

    @Mock
    private lateinit var triggerPostalApplicationStatisticsUpdateQueue: MessageQueue<UpdateStatisticsMessage>

    @BeforeEach
    fun setUp() {
        statisticsUpdateService = StatisticsUpdateService(
            triggerVoterCardStatisticsUpdateQueue,
            triggerPostalApplicationStatisticsUpdateQueue
        )
    }

    @Test
    fun `should send a message containing the application ID to the voter card queue for voter card applications`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD)

        // Then
        verify(triggerVoterCardStatisticsUpdateQueue).submit(
            eq(UpdateStatisticsMessage(applicationId)),
            any()
        )
        verifyNoMoreInteractions(triggerVoterCardStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the voter card queue for non voter card applications`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL)

        // Then
        verifyNoInteractions(triggerVoterCardStatisticsUpdateQueue)
    }

    @Test
    fun `should send a message containing the application ID to the postal queue for postal applications`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL)

        // Then
        verify(triggerPostalApplicationStatisticsUpdateQueue).submit(
            eq(UpdateStatisticsMessage(applicationId)),
            any()
        )
        verifyNoMoreInteractions(triggerPostalApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the postal queue for non postal applications`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD)

        // Then
        verifyNoInteractions(triggerPostalApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should include message-group-id and message-deduplication-id headers on the message`() {

        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD)

        // Then
        verify(triggerVoterCardStatisticsUpdateQueue).submit(
            any(),
            capture(headersArgumentCaptor)
        )
        assertThat(headersArgumentCaptor.value.get("message-group-id")).isEqualTo(applicationId)
        assertThat(headersArgumentCaptor.value.get("message-deduplication-id")).isNotNull
        verifyNoMoreInteractions(triggerUpdateStatisticsMessageQueue)
    }
}
