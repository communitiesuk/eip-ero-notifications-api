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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedDocumentMessage
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class SendNotifyRejectedDocumentMessageListenerIntegrationTest : IntegrationTest() {

    @Test
    fun `should process rejected document message to send Email for english language and save notification`() {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.POSTAL
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyRejectedDocumentMessage(
            channel = NotificationChannel.EMAIL,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyRejectedDocumentQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL, listOf(gssCode)
            )
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(
                    uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL,
                    NotificationType.REJECTED_DOCUMENT,
                    Channel.EMAIL
                )
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch")
        }
    }

    @Test
    fun `should process rejected document message to send Letter for english language and save notification`() {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.POSTAL
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyRejectedDocumentMessage(
            channel = NotificationChannel.LETTER,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyRejectedDocumentQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendLetterCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL, listOf(gssCode)
            )
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(
                    uk.gov.dluhc.notificationsapi.database.entity.SourceType.POSTAL,
                    NotificationType.REJECTED_DOCUMENT,
                    Channel.LETTER
                )
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch")
        }
    }
}
