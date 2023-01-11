package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRemoveApplicationNotificationsMessage
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class RemoveApplicationNotificationsMessageListenerIntegrationTest : IntegrationTest() {

    @Test
    fun `should remove notifications for application`() {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        notificationRepository.saveNotification(aNotificationBuilder(gssCode = gssCode, sourceReference = sourceReference))
        notificationRepository.saveNotification(aNotificationBuilder(gssCode = gssCode, sourceReference = sourceReference))
        val sqsMessage = buildRemoveApplicationNotificationsMessage(
            gssCode = gssCode,
            sourceReference = sourceReference
        )

        // When
        sqsMessagingTemplate.convertAndSend(removeApplicationNotificationsQueueName, sqsMessage)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            assertThat(notificationRepository.getBySourceReference(sourceReference, gssCode)).isEmpty()
            stopWatch.stop()
            logger.info("Completed assertions in $stopWatch for 2 removed notifications")
        }
    }
}
