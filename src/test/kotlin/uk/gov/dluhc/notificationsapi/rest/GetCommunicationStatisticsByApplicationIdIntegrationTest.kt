package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.internalauth.Constants
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import java.util.UUID

internal class GetCommunicationStatisticsByApplicationIdIntegrationTest : IntegrationTest() {

    @BeforeEach
    fun resetWiremock() {
        wireMockService.resetAllStubsAndMappings()
    }

    @ParameterizedTest
    @CsvSource(
        value = ["postal", "proxy", "voter-card", "overseas"],
    )
    fun `should return unauthorised given no auth header present`(
        applicationType: String,
    ) {
        // When / Then
        webTestClient.get()
            .uri(buildUri(applicationId = aRandomSourceReference(), service = applicationType))
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @ParameterizedTest
    @CsvSource(
        value = ["postal", "proxy", "voter-card", "overseas"],
    )
    fun `should return unauthorised given failed sts auth request`(
        applicationType: String,
    ) {
        // Given
        wireMockService.stubUnsuccessfulStsPresignedAuthResponse()

        // When / Then
        webTestClient.get()
            .uri(buildUri(applicationId = aRandomSourceReference(), service = applicationType))
            .header(Constants.AUTH_HEADER_NAME, "query=something")
            .exchange()
            .expectStatus()
            .isUnauthorized()
    }

    @ParameterizedTest
    @CsvSource(
        value = ["postal", "proxy", "voter-card", "overseas"],
    )
    fun `should return forbidden given role is not allowed to access statistics`(
        applicationType: String,
    ) {
        // Given
        wireMockService.stubSuccessfulStsPresignedAuthResponse("wrong-role")

        // When / Then
        webTestClient.get()
            .uri(buildUri(applicationId = aRandomSourceReference(), service = applicationType))
            .header(Constants.AUTH_HEADER_NAME, "query=something")
            .exchange()
            .expectStatus()
            .isForbidden()
    }

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

        wireMockService.stubSuccessfulStsPresignedAuthResponse("stats-role")

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, service = applicationType))
            .header(Constants.AUTH_HEADER_NAME, "query=something")
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["POSTAL,postal", "PROXY,proxy"],
    )
    fun `should return correctly built response including legacy communications`(
        sourceType: SourceType,
        applicationType: String,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        notificationRepository.saveNotification(
            aNotificationBuilder(
                sourceReference = applicationId,
                sourceType = sourceType,
                type = NotificationType.REJECTED_SIGNATURE,
            ),
        )
        notificationRepository.saveNotification(
            aNotificationBuilder(
                sourceReference = applicationId,
                sourceType = sourceType,
                type = NotificationType.REQUESTED_SIGNATURE,
            ),
        )

        val expected = CommunicationsStatisticsResponse(
            numBespokeCommunicationsSent = 0,
            numNotRegisteredToVoteCommsSent = 0,
            numSignatureRequestCommsSent = 2,
            numPhotoRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
        )

        wireMockService.stubSuccessfulStsPresignedAuthResponse("stats-role")

        // When
        val response = webTestClient.get()
            .uri(buildUri(applicationId = applicationId, service = applicationType))
            .header(Constants.AUTH_HEADER_NAME, "query=something")
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponse::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildUri(applicationId: String = UUID.randomUUID().toString(), service: String) =
        "/communications/statistics/$service/$applicationId"
}
