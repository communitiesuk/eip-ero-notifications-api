package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.Channel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyMessage
import java.time.Duration
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class SendNotifyMessageListenerIntegrationTest : IntegrationTest() {

    @Test
    fun `should process message to send Email and save notification`() {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val notificationSourceType = VOTER_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyMessage(
            channel = Channel.EMAIL,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        deleteNotifications(notificationRepository.getBySourceReference(sourceReference, gssCode))
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyMessageQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(5, TimeUnit.SECONDS).untilAsserted {
            val actualEntity = notificationRepository.getBySourceReference(sourceReference, gssCode)
            assertThat(actualEntity).hasSize(1)
            wireMockService.verifyNotifySendEmailCalled()
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch")
        }
    }

    @Test
    fun `should fail to process message as UK Gov Notify service raised non-retryable error`() {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyMessage(
            channel = Channel.EMAIL,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailBadRequestResponse()

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyMessageQueueName, payload)

        // Then
        await.pollDelay(Duration.ofSeconds(1)).atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalledExactly(1)
        }
        val actualEntity = notificationRepository.getBySourceReference(gssCode, VOTER_CARD, sourceReference)
        assertThat(actualEntity).isNull()
    }

    @Test
    fun `should process message as UK Gov Notify service raised retryable error`() {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyMessage(
            channel = Channel.EMAIL,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailInternalErrorResponse()

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyMessageQueueName, payload)

        // Then
        await.pollDelay(Duration.ofSeconds(1)).atMost(5, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalledMoreThan(1)
        }
        val actualEntity = notificationRepository.getBySourceReference(gssCode, VOTER_CARD, sourceReference)
        assertThat(actualEntity).isNull()
    }
}
