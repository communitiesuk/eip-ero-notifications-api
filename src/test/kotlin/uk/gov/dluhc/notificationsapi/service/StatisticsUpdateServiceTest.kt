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
import uk.gov.dluhc.overseasapplicationsapi.messaging.models.UpdateStatisticsMessage as OverseasUpdateStatisticsMessage
import uk.gov.dluhc.postalapplicationsapi.messaging.models.UpdateStatisticsMessage as PostalUpdateStatisticsMessage
import uk.gov.dluhc.proxyapplicationsapi.messaging.models.UpdateStatisticsMessage as ProxyUpdateStatisticsMessage
import uk.gov.dluhc.votercardapplicationsapi.messaging.models.UpdateStatisticsMessage as VoterCardUpdateStatisticsMessage

@ExtendWith(MockitoExtension::class)
class StatisticsUpdateServiceTest {

    @InjectMocks
    private lateinit var statisticsUpdateService: StatisticsUpdateService

    @Captor
    private lateinit var headersArgumentCaptor: ArgumentCaptor<Map<String, Any>>

    @Mock
    private lateinit var triggerVoterCardStatisticsUpdateQueue: MessageQueue<VoterCardUpdateStatisticsMessage>

    @Mock
    private lateinit var triggerPostalApplicationStatisticsUpdateQueue: MessageQueue<PostalUpdateStatisticsMessage>

    @Mock
    private lateinit var triggerProxyApplicationStatisticsUpdateQueue: MessageQueue<ProxyUpdateStatisticsMessage>

    @Mock
    private lateinit var triggerOverseasApplicationStatisticsUpdateQueue: MessageQueue<OverseasUpdateStatisticsMessage>

    @Mock
    private lateinit var triggerApplicationStatisticsUpdateQueue: MessageQueue<ApplicationUpdateStatisticsMessage>

    @BeforeEach
    fun setUp() {
        statisticsUpdateService = StatisticsUpdateService(
            triggerVoterCardStatisticsUpdateQueue,
            triggerPostalApplicationStatisticsUpdateQueue,
            triggerProxyApplicationStatisticsUpdateQueue,
            triggerOverseasApplicationStatisticsUpdateQueue,
            triggerApplicationStatisticsUpdateQueue,
        )
    }

    @Test
    fun `should send a message containing the application ID to the voter card queue for voter card applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, false)

        // Then
        verify(triggerVoterCardStatisticsUpdateQueue).submit(
            eq(VoterCardUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerVoterCardStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the voter card queue for non voter card applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, false)

        // Then
        verifyNoInteractions(triggerVoterCardStatisticsUpdateQueue)
    }

    @Test
    fun `should send a message containing the application ID to the postal queue for postal applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, false)

        // Then
        verify(triggerPostalApplicationStatisticsUpdateQueue).submit(
            eq(PostalUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerPostalApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the postal queue for non postal applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, false)

        // Then
        verifyNoInteractions(triggerPostalApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should send a message containing the application ID to the proxy queue for proxy applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, false)

        // Then
        verify(triggerProxyApplicationStatisticsUpdateQueue).submit(
            eq(ProxyUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerProxyApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the proxy queue for non proxy applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, false)

        // Then
        verifyNoInteractions(triggerProxyApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should send a message containing the application ID to the overseas queue for overseas applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, false)

        // Then
        verify(triggerOverseasApplicationStatisticsUpdateQueue).submit(
            eq(OverseasUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerOverseasApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the overseas queue for non overseas applications`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, false)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, false)

        // Then
        verifyNoInteractions(triggerOverseasApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should send a message to the applications queue for appplications flagged for OS`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, true)

        // Then
        verify(triggerApplicationStatisticsUpdateQueue).submit(
            eq(ApplicationUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should not send a message to the any other queue for appplications flagged for OS`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.POSTAL, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.PROXY, true)
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.OVERSEAS, true)

        // Then
        verifyNoInteractions(triggerVoterCardStatisticsUpdateQueue)
        verifyNoInteractions(triggerPostalApplicationStatisticsUpdateQueue)
        verifyNoInteractions(triggerProxyApplicationStatisticsUpdateQueue)
        verifyNoInteractions(triggerOverseasApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should include message-group-id and message-deduplication-id headers on the message`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId, SourceType.VOTER_CARD, false)

        // Then
        verify(triggerVoterCardStatisticsUpdateQueue).submit(
            any(),
            capture(headersArgumentCaptor),
        )
        assertThat(headersArgumentCaptor.value.get("message-group-id")).isEqualTo(applicationId)
        assertThat(headersArgumentCaptor.value.get("message-deduplication-id")).isNotNull
        verifyNoMoreInteractions(triggerVoterCardStatisticsUpdateQueue)
    }
}
