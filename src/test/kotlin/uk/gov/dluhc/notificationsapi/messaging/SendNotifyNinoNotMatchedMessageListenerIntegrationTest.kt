package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyNinoNotMatchedMessage
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class SendNotifyNinoNotMatchedMessageListenerIntegrationTest : IntegrationTest() {

    @Test
    fun `should process Nino not matched message to send Email and save notification`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val expectedSourceType = uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL
        val payload = buildSendNotifyNinoNotMatchedMessage(
            channel = NotificationChannel.EMAIL,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = SourceType.POSTAL,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyNinoNotMatchedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(expectedSourceType, NotificationType.NINO_NOT_MATCHED, Channel.EMAIL)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language EN")
        }
    }

    @Test
    fun `should process nino not matched message to send Letter and save notification`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val expectedSourceType = uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL
        val payload = buildSendNotifyNinoNotMatchedMessage(
            channel = NotificationChannel.LETTER,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = SourceType.POSTAL,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyNinoNotMatchedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendLetterCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(expectedSourceType, NotificationType.NINO_NOT_MATCHED, Channel.LETTER)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language EN")
        }
    }
}
