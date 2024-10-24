package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.api.AfterEach
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildBespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyBespokeCommMessage
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum

private val logger = KotlinLogging.logger {}

internal class SendNotifyBespokeCommMessageListenerIntegrationTest : IntegrationTest() {

    @BeforeEach
    @AfterEach
    fun cleanUp() {
        clearSqsQueue(sendUkGovNotifyBespokeCommQueueName)
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
            "EMAIL,EN,OVERSEAS,OVERSEAS",
            "EMAIL,CY,OVERSEAS,OVERSEAS",
            "LETTER,EN,OVERSEAS,OVERSEAS",
            "LETTER,CY,OVERSEAS,OVERSEAS",
            "EMAIL,EN,VOTER_MINUS_CARD,VOTER_CARD",
            "EMAIL,CY,VOTER_MINUS_CARD,VOTER_CARD",
            "LETTER,EN,VOTER_MINUS_CARD,VOTER_CARD",
            "LETTER,CY,VOTER_MINUS_CARD,VOTER_CARD",
        ],
    )
    fun `should process bespoke comm message from relevant service`(
        sqsChannel: CommunicationChannel,
        language: Language,
        sourceType: SourceType,
        expectedSourceType: SourceTypeEntityEnum,
    ) {
        val personalisationMessage = buildBespokeCommPersonalisation()

        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyBespokeCommMessage(
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
        sqsMessagingTemplate.send(sendUkGovNotifyBespokeCommQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(10, TimeUnit.SECONDS).untilAsserted {
            if (sqsChannel == CommunicationChannel.EMAIL) {
                wireMockService.verifyNotifySendEmailCalled()
            } else {
                wireMockService.verifyNotifySendLetterCalled()
            }
            val actualEntity = notificationRepository
                .getBySourceReferenceAndGssCode(sourceReference, expectedSourceType, listOf(gssCode))
            Assertions.assertThat(actualEntity).hasSize(1)
            when (sourceType) {
                SourceType.POSTAL -> assertPostalUpdateStatisticsMessageSent(sourceReference)
                SourceType.PROXY -> assertProxyUpdateStatisticsMessageSent(sourceReference)
                SourceType.VOTER_MINUS_CARD -> assertVoterCardUpdateStatisticsMessageSent(sourceReference)
                SourceType.OVERSEAS -> assertOverseasUpdateStatisticsMessageSent(sourceReference)
                else -> {}
            }
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language and channel $sqsChannel")
        }
    }
}
