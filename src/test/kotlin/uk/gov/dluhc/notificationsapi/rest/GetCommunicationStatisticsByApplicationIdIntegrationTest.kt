package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import java.util.UUID

internal class GetCommunicationStatisticsByApplicationIdIntegrationTest : IntegrationTest() {
    @ParameterizedTest
    @CsvSource(
        value = ["POSTAL,postal", "PROXY,proxy", "VOTER_CARD,voter-card", "OVERSEAS,overseas"],
    )
    fun `should return correctly built response`(
        sourceType: SourceType,
        applicationType: String,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        notificationRepository.saveNotification(
            aNotificationBuilder(
                sourceReference = applicationId,
                sourceType = sourceType,
                type = NotificationType.BESPOKE_COMM,
            ),
        )

        val expected = CommunicationsStatisticsResponse(
            numBespokeCommunicationsSent = 1,
            numNotRegisteredToVoteCommsSent = 0,
            numSignatureRequestCommsSent = 0,
            numPhotoRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
        )

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, service = applicationType))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildUri(applicationId: String = UUID.randomUUID().toString(), service: String) =
        "/communications/statistics/$service/$applicationId"
}
