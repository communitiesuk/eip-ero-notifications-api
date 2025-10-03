package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifySignatureResubmissionMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSignatureResubmissionPersonalisation
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity

private val logger = KotlinLogging.logger {}

internal class SendNotifySignatureResubmissionMessageListenerIntegrationTest : IntegrationTest() {

    @BeforeEach
    fun cleanUp() {
        clearSqsQueueAsync(sendUkGovNotifySignatureResubmissionQueueName).join()
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
        ],
    )
    fun `should process signature resubmission notification message from relevant service`(
        sqsChannel: CommunicationChannel,
        language: Language,
        sourceType: SourceType,
        expectedSourceType: SourceTypeEntity,
    ) {
        val personalisationMessage = buildSignatureResubmissionPersonalisation()

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifySignatureResubmissionMessage(
            channel = sqsChannel,
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        if (sqsChannel == CommunicationChannel.EMAIL) {
            wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())
        } else {
            wireMockService.stubNotifySendLetterResponse(NotifySendLetterSuccessResponse())
        }

        // When
        sqsMessagingTemplate.send(sendUkGovNotifySignatureResubmissionQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(5, TimeUnit.SECONDS).untilAsserted {
            if (sqsChannel == CommunicationChannel.EMAIL) {
                wireMockService.verifyNotifySendEmailCalled()
            } else {
                wireMockService.verifyNotifySendLetterCalled()
            }
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            Assertions.assertThat(actualEntity).hasSize(1)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language and channel $sqsChannel")
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "LETTER,EN,OVERSEAS,OVERSEAS",
            "LETTER,CY,OVERSEAS,OVERSEAS",
            "EMAIL,EN,VOTER_MINUS_CARD,VOTER_CARD",
        ],
    )
    fun `should not process signature resubmission notification message from non enabled services`(
        sqsChannel: CommunicationChannel,
        language: Language,
        sourceType: SourceType,
        sourceTypeEntity: SourceTypeEntity,
    ) {
        val personalisationMessage = buildSignatureResubmissionPersonalisation()

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifySignatureResubmissionMessage(
            channel = sqsChannel,
            language = language,
            sourceType = sourceType,
            sourceReference = sourceReference,
            gssCode = gssCode,
            personalisation = personalisationMessage,
        )

        // When
        sqsMessagingTemplate.send(sendUkGovNotifySignatureResubmissionQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            if (sqsChannel == CommunicationChannel.EMAIL) {
                wireMockService.verifyNotifySendEmailNeverCalled()
            } else {
                wireMockService.verifyNotifySendLetterNeverCalled()
            }
            val actualEntity =
                notificationRepository.getBySourceReferenceAndGssCode(
                    sourceReference,
                    sourceTypeEntity,
                    listOf(gssCode),
                )
            Assertions.assertThat(actualEntity).hasSize(0)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language and $sqsChannel")
        }
    }
}
