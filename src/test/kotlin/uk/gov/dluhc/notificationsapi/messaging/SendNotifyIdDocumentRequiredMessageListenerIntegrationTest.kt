package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel.EMAIL
import uk.gov.dluhc.notificationsapi.database.entity.Channel.LETTER
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType.ID_DOCUMENT_REQUIRED
import uk.gov.dluhc.notificationsapi.database.entity.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyIdDocumentRequiredMessage
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

internal class SendNotifyIdDocumentRequiredMessageListenerIntegrationTest : IntegrationTest() {

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should process ID document required message to send Email for given language and save notification`(
        language: Language
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyIdDocumentRequiredMessage(
            channel = NotificationChannel.EMAIL,
            language = language,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyIdDocumentRequiredQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                VOTER_CARD,
                listOf(gssCode)
            )
            assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(VOTER_CARD, ID_DOCUMENT_REQUIRED, EMAIL)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should process ID document required message to send Letter for given language and save notification`(
        language: Language
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceType = SourceType.VOTER_MINUS_CARD
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyIdDocumentRequiredMessage(
            channel = NotificationChannel.LETTER,
            language = language,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyIdDocumentRequiredQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendLetterCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                VOTER_CARD,
                listOf(gssCode)
            )
            assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(VOTER_CARD, ID_DOCUMENT_REQUIRED, LETTER)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }
}
