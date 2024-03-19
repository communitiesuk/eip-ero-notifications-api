package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentCategory
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyNinoNotMatchedMessage
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity

private val logger = KotlinLogging.logger {}

internal class SendNotifyNinoNotMatchedMessageListenerIntegrationTest : IntegrationTest() {

    companion object {
        private const val SOURCE_TYPE_POSTAL = "POSTAL"
        private const val SOURCE_TYPE_PROXY = "PROXY"
        private const val SOURCE_TYPE_OVERSEAS = "OVERSEAS"
        private const val NOTIFICATION_TYPE_IDENTITY = "NINO_NOT_MATCHED"
        private const val NOTIFICATION_TYPE_PREVIOUS_ADDRESS = "PREVIOUS_ADDRESS_DOCUMENT_REQUIRED"
        private const val NOTIFICATION_TYPE_PARENT_GUARDIAN_REQUIRED = "PARENT_GUARDIAN_PROOF_REQUIRED"
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "$SOURCE_TYPE_POSTAL, $SOURCE_TYPE_POSTAL, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_PROXY, $SOURCE_TYPE_PROXY, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, PREVIOUS_MINUS_ADDRESS, $NOTIFICATION_TYPE_PREVIOUS_ADDRESS",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, PARENT_MINUS_GUARDIAN, $NOTIFICATION_TYPE_PARENT_GUARDIAN_REQUIRED",
        ],
    )
    fun `should process required document message to send Email and save notification`(
        sourceType: SourceType,
        sourceTypeEntity: SourceTypeEntity,
        documentCategory: DocumentCategory,
        notificationType: NotificationType
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyNinoNotMatchedMessage(
            channel = NotificationChannel.EMAIL,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference,
            documentCategory = documentCategory
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyNinoNotMatchedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(
                    sourceReference,
                    sourceTypeEntity,
                    listOf(gssCode)
                )
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(sourceTypeEntity, notificationType, Channel.EMAIL)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language EN")
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "$SOURCE_TYPE_POSTAL, $SOURCE_TYPE_POSTAL, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_PROXY, $SOURCE_TYPE_PROXY, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, IDENTITY, $NOTIFICATION_TYPE_IDENTITY",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, PREVIOUS_MINUS_ADDRESS, $NOTIFICATION_TYPE_PREVIOUS_ADDRESS",
            "$SOURCE_TYPE_OVERSEAS, $SOURCE_TYPE_OVERSEAS, PARENT_MINUS_GUARDIAN, $NOTIFICATION_TYPE_PARENT_GUARDIAN_REQUIRED",
        ],
    )
    fun `should process required document message to send Letter and save notification`(
        sourceType: SourceType,
        sourceTypeEntity: SourceTypeEntity,
        documentCategory: DocumentCategory,
        notificationType: NotificationType
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyNinoNotMatchedMessage(
            channel = NotificationChannel.LETTER,
            language = Language.EN,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference,
            documentCategory = documentCategory
        )
        wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyNinoNotMatchedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendLetterCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(
                    sourceReference,
                    sourceTypeEntity,
                    listOf(gssCode)
                )
            Assertions.assertThat(actualEntity).hasSize(1).element(0)
                .extracting("sourceType", "type", "channel")
                .containsExactlyInAnyOrder(sourceTypeEntity, notificationType, Channel.LETTER)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language EN")
        }
    }
}
