package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponseOAVA
import uk.gov.dluhc.notificationsapi.models.CommunicationsStatisticsResponseVAC
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity.aNotificationBuilder
import java.util.UUID

internal class GetCommunicationStatisticsByApplicationIdIntegrationTest : IntegrationTest() {

    @Test
    fun `should return photo not requested and documents not requested for VAC application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildVacUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseVAC::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return photo not requested and documents not requested for VAC application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.APPLICATION_APPROVED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildVacUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseVAC::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return photo requested for VAC application with photo resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.PHOTO_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = true,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildVacUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseVAC::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for VAC application with id document resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.ID_DOCUMENT_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildVacUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseVAC::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for VAC application with id document required communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.ID_DOCUMENT_REQUIRED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildVacUri(applicationId = applicationId))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseVAC::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return signature not requested and documents not requested for Postal application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "postal"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actualPostal = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actualPostal).isEqualTo(expected)
    }

    @Test
    fun `should return signature not requested and documents not requested for Postal application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.APPLICATION_APPROVED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "postal"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return signature requested for Postal application with signature resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.REQUESTED_SIGNATURE
        )

        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = true,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "postal"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for Postal application with id document resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.ID_DOCUMENT_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "postal"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for Postal application with id document required communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.ID_DOCUMENT_REQUIRED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "postal"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return signature not requested and documents not requested for Proxy application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "proxy"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actualPostal = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actualPostal).isEqualTo(expected)
    }

    @Test
    fun `should return signature not requested and documents not requested for Proxy application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.APPLICATION_APPROVED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "proxy"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return signature requested for Proxy application with signature resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.REQUESTED_SIGNATURE
        )

        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = true,
            identityDocumentsRequested = false
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "proxy"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for Proxy application with id document resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.ID_DOCUMENT_RESUBMISSION
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "proxy"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should return identity document requested for Proxy application with id document required communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.ID_DOCUMENT_REQUIRED
        )
        notificationRepository.saveNotification(
            sentNotification
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true
        )

        // When
        val response = webTestClient.get()
            .uri(buildOavaUri(applicationId = applicationId, "proxy"))
            .exchange()

        // Then
        response.expectStatus().isOk
        val actual = response.returnResult(CommunicationsStatisticsResponseOAVA::class.java).responseBody.blockFirst()
        assertThat(actual).isEqualTo(expected)
    }

    private fun buildVacUri(applicationId: String = UUID.randomUUID().toString()) =
        "/communications/statistics/vac?applicationId=$applicationId"

    private fun buildOavaUri(applicationId: String = UUID.randomUUID().toString(), oavaService: String) =
        "/communications/statistics/oava/$oavaService?applicationId=$applicationId"
}
