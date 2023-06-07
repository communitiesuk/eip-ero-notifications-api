package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyRejectedSignatureMessage
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessaging

private val logger = KotlinLogging.logger {}

internal class SendNotifyRejectedSignatureMessageListenerIntegrationTest : IntegrationTest() {

    @BeforeEach
    @AfterEach
    fun cleanUp() {
        clearSqsQueue(sendUkGovNotifyRejectedSignatureQueueName)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL,EN,PROXY,PROXY",
            "EMAIL,CY,PROXY,PROXY",
            "LETTER,EN,PROXY,PROXY",
            "LETTER,CY,PROXY,PROXY",
            "EMAIL,EN,POSTAL,POSTAL",
            "EMAIL,CY,POSTAL,POSTAL",
            "LETTER,EN,POSTAL,POSTAL",
            "LETTER,CY,POSTAL,POSTAL",
        ]
    )
    fun `should process rejected signature notification message from relevant service`(
        sqsChannel: NotificationChannel,
        language: Language,
        sourceType: SourceTypeMessaging,
        expectedSourceType: SourceTypeEntity
    ) {
        val rejectionReasons = listOf("Reason1", "Reason2")
        val rejectionNotes = "Invalid Signature"
        val personalisationMessage = buildRejectedSignaturePersonalisation(
            rejectionReasons = rejectionReasons,
            rejectionNotes = rejectionNotes
        )

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyRejectedSignatureMessage(
            channel = sqsChannel,
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        if (sqsChannel == NotificationChannel.EMAIL)
            wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())
        else
            wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyRejectedSignatureQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(5, TimeUnit.SECONDS).untilAsserted {
            if (sqsChannel == NotificationChannel.EMAIL)
                wireMockService.verifyNotifySendEmailCalled()
            else
                wireMockService.verifyNotifySendLetterCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            assertThat(actualEntity).hasSize(1)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language and channel $sqsChannel")
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "LETTER,EN,OVERSEAS",
            "LETTER,CY,OVERSEAS",
        ]
    )
    fun `should not process rejected signature notification message from non enabled services`(
        sqsChannel: NotificationChannel,
        language: Language,
        sourceType: SourceTypeMessaging
    ) {
        val rejectionReasons = listOf("Reason1", "Reason2")
        val rejectionNotes = "Invalid Signature"
        val personalisationMessage = buildRejectedSignaturePersonalisation(
            rejectionReasons = rejectionReasons,
            rejectionNotes = rejectionNotes
        )

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyRejectedSignatureMessage(
            channel = sqsChannel,
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyRejectedSignatureQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            if (sqsChannel == NotificationChannel.EMAIL)
                wireMockService.verifyNotifySendEmailNeverCalled()
            else
                wireMockService.verifyNotifySendLetterNeverCalled()
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(
                    sourceReference,
                    SourceTypeEntity.valueOf(sourceType.name),
                    listOf(gssCode)
                )
            assertThat(actualEntity).hasSize(0)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language and $sqsChannel")
        }
    }
}
