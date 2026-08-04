package uk.gov.dluhc.notificationsapi.messaging

import ch.qos.logback.classic.Level
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.testcontainers.shaded.org.apache.commons.lang3.time.StopWatch
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.TestLogAppender
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildBasePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifySignatureReceivedMessage
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity

private val logger = KotlinLogging.logger {}

internal class SendNotifySignatureReceivedMessageListenerIntegrationTest : IntegrationTest() {
    @BeforeEach
    fun cleanUp() {
        clearSqsQueueAsync(sendUkGovNotifySignatureReceivedQueueName).join()
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EN,POSTAL,POSTAL",
            "CY,POSTAL,POSTAL",
            "EN,PROXY,PROXY",
            "CY,PROXY,PROXY",
        ],
    )
    fun `should process signature received notification message from relevant service`(
        language: Language,
        sourceType: SourceType,
        expectedSourceType: SourceTypeEntity,
    ) {
        val personalisationMessage = buildBasePersonalisation()

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifySignatureReceivedMessage(
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        val expectedLog = "Received request to send UK GOV Notify Signature Resubmission comms for gssCode: [$gssCode] with " +
            "channel: [EMAIL], " +
            "messageType: [SIGNATURE_MINUS_RECEIVED], " +
            "language: [$language]"

        // When
        sqsMessagingTemplate.send(sendUkGovNotifySignatureReceivedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            assertThat(actualEntity).hasSize(1)
            assertThat(TestLogAppender.hasLog(expectedLog, Level.INFO)).isTrue()
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EN,OVERSEAS,OVERSEAS",
            "CY,OVERSEAS,OVERSEAS",
            "EN,VOTER_MINUS_CARD,VOTER_CARD",
        ],
    )
    fun `should not process signature received notification message from non-enabled service`(
        language: Language,
        sourceType: SourceType,
        expectedSourceType: SourceTypeEntity,
    ) {
        val personalisationMessage = buildBasePersonalisation()

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifySignatureReceivedMessage(
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        // When
        sqsMessagingTemplate.send(sendUkGovNotifySignatureReceivedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailNeverCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            assertThat(actualEntity).hasSize(0)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }
}
