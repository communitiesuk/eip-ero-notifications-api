package uk.gov.dluhc.notificationsapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
import uk.gov.dluhc.applicationsapi.messaging.models.UpdateStatisticsMessage as ApplicationUpdateStatisticsMessage

@ExtendWith(MockitoExtension::class)
class StatisticsUpdateServiceTest {

    @InjectMocks
    private lateinit var statisticsUpdateService: StatisticsUpdateService

    @Captor
    private lateinit var headersArgumentCaptor: ArgumentCaptor<Map<String, Any>>

    @Mock
    private lateinit var triggerApplicationStatisticsUpdateQueue: MessageQueue<ApplicationUpdateStatisticsMessage>

    @BeforeEach
    fun setUp() {
        statisticsUpdateService = StatisticsUpdateService(
            triggerApplicationStatisticsUpdateQueue,
        )
    }

    @ParameterizedTest
    @EnumSource(value = SourceType::class)
    fun `should send a message to the trigger application update queue when stats update triggered`(sourceType: SourceType) {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId)

        // Then
        verify(triggerApplicationStatisticsUpdateQueue).submit(
            eq(ApplicationUpdateStatisticsMessage(applicationId)),
            any(),
        )
        verifyNoMoreInteractions(triggerApplicationStatisticsUpdateQueue)
    }

    @Test
    fun `should include message-group-id and message-deduplication-id headers on the message`() {
        // Given
        val applicationId = aRandomSourceReference()

        // When
        statisticsUpdateService.triggerStatisticsUpdate(applicationId)

        // Then
        verify(triggerApplicationStatisticsUpdateQueue).submit(
            any(),
            capture(headersArgumentCaptor),
        )
        assertThat(headersArgumentCaptor.value.get("message-group-id")).isEqualTo(applicationId)
        assertThat(headersArgumentCaptor.value.get("message-deduplication-id")).isNotNull
        verifyNoMoreInteractions(triggerApplicationStatisticsUpdateQueue)
    }
}
