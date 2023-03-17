package uk.gov.dluhc.notificationsapi.messaging

import mu.KotlinLogging
import org.apache.commons.lang3.time.StopWatch
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.messaging.models.Language
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSendNotifyApplicationReceivedMessage
import java.util.concurrent.TimeUnit
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntity

private val logger = KotlinLogging.logger {}

internal class SendNotifyApplicationReceivedMessageListenerIntegrationTest : IntegrationTest() {

    @ParameterizedTest
    @CsvSource(
        "CY, POSTAL, POSTAL",
        "CY, PROXY, PROXY",
        "CY, OVERSEAS, OVERSEAS",
        "EN, POSTAL, POSTAL",
        "EN, PROXY, PROXY",
        "EN, OVERSEAS, OVERSEAS"
    )
    fun `should process application received message to send Email for given language and save notification`(
        language: Language,
        sourceType: SourceType,
        sourceTypeEntity: SourceTypeEntity
    ) {
        // Given
        val gssCode = aGssCode()
        val sourceReference = aRandomSourceReference()
        val payload = buildSendNotifyApplicationReceivedMessage(
            language = language,
            gssCode = gssCode,
            sourceType = sourceType,
            sourceReference = sourceReference
        )
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        sqsMessagingTemplate.convertAndSend(sendUkGovNotifyApplicationReceivedQueueName, payload)

        // Then
        val stopWatch = StopWatch.createStarted()
        await.atMost(3, TimeUnit.SECONDS).untilAsserted {
            wireMockService.verifyNotifySendEmailCalled()
            val actualEntity = notificationRepository.getBySourceReferenceAndGssCode(
                sourceReference,
                sourceTypeEntity, listOf(gssCode)
            )
            Assertions.assertThat(actualEntity).hasSize(1)
            stopWatch.stop()
            logger.info("completed assertions in $stopWatch for language $language")
        }
    }
}
