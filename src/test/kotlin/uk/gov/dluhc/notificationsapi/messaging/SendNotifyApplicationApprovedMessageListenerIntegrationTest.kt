package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyApplicationApprovedMessage
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class SendNotifyApplicationApprovedMessageListenerIntegrationTest : IntegrationTest() {

    @BeforeEach
    fun cleanUp() {
        clearSqsQueueAsync(sendUkGovNotifyApplicationApprovedQueueName).join()
    }

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should process application approved message to send Email for given language and save notification`(
        language: Language,
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyApplicationApprovedMessage(
            language = language,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference,
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.send(sendUkGovNotifyApplicationApprovedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD,
                listOf(gssCode),
            )
            Assertions.assertThat(actualEntity).hasSize(1)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }
}
