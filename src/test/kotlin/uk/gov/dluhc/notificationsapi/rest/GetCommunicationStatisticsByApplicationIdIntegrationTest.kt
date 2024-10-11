package uk.gov.dluhc.notificationsapi.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
    fun `should return photo not requested, documents not requested, 0 bespoke communications has not sent not registered to vote comm for VAC application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return photo not requested, documents not requested, 0 bespoke communications sent and has not sent not registered to vote comm for VAC application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.APPLICATION_APPROVED,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return photo requested and correct number of comms sent for VAC application with photo resubmission communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.PHOTO_RESUBMISSION,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val sentNotification2 = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.PHOTO_RESUBMISSION,
        )
        notificationRepository.saveNotification(
            sentNotification2,
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = true,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = 0,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for VAC application with id document resubmission communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.VOTER_CARD,
                type = NotificationType.ID_DOCUMENT_RESUBMISSION,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for VAC application with id document required communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.VOTER_CARD,
                type = NotificationType.ID_DOCUMENT_REQUIRED,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return correct number for bespoke communications sent for VAC application that has sent bespoke communications`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.VOTER_CARD,
                type = NotificationType.BESPOKE_COMM,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = numberOfNotifications,
            hasSentNotRegisteredToVoteCommunication = false,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return not registered to vote comm sent for VAC application with not registered to vote communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.VOTER_CARD,
            type = NotificationType.NOT_REGISTERED_TO_VOTE,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseVAC(
            photoRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = true,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return signature not requested, documents not requested, 0 bespoke communications sent and not sent not registered to vote comm for Postal application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return signature not requested, documents not requested, 0 bespoke communications sent and not sent not registered to vote comm for Postal application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.APPLICATION_APPROVED,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return signature requested and correct number of comms sent for Postal application with signature resubmission communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.POSTAL,
                type = NotificationType.REQUESTED_SIGNATURE,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = true,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = numberOfNotifications,
            numIdentityDocumentRequestCommsSent = 0,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for Postal application with id document resubmission communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.POSTAL,
                type = NotificationType.ID_DOCUMENT_RESUBMISSION,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for Postal application with id document required communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.POSTAL,
                type = NotificationType.ID_DOCUMENT_REQUIRED,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return correct number for bespoke communications sent for Postal application that has sent bespoke communications`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.POSTAL,
                type = NotificationType.BESPOKE_COMM,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = numberOfNotifications,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return not registered to vote comm sent for Postal application with not registered to vote communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.POSTAL,
            type = NotificationType.NOT_REGISTERED_TO_VOTE,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = true,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return signature not requested, documents not requested, 0 bespoke communications sent and not sent not registered to vote comm for Proxy application with no communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return signature not requested, documents not requested, 0 bespoke communications sent and not sent not registered to vote comm for Proxy application with no relevant communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.APPLICATION_APPROVED,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return signature requested and correct number of comms sent for Proxy application with signature requested communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.PROXY,
                type = NotificationType.REQUESTED_SIGNATURE,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = true,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = numberOfNotifications,
            numIdentityDocumentRequestCommsSent = 0,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for Proxy application with id document resubmission communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.PROXY,
                type = NotificationType.ID_DOCUMENT_RESUBMISSION,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return identity document requested and correct number of comms sent for Proxy application with id document required communications sent`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.PROXY,
                type = NotificationType.ID_DOCUMENT_REQUIRED,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = true,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = numberOfNotifications,
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

    @ParameterizedTest
    @CsvSource(
        value = [
            "1",
            "2",
            "3",
        ],
    )
    fun `should return correct number for bespoke communications sent for Proxy application that has sent bespoke communications`(
        numberOfNotifications: Int,
    ) {
        // Given
        val applicationId = aRandomSourceReference()

        repeat(numberOfNotifications) {
            val sentNotification = aNotificationBuilder(
                id = UUID.randomUUID(),
                sourceReference = applicationId,
                sourceType = SourceType.PROXY,
                type = NotificationType.BESPOKE_COMM,
            )
            notificationRepository.saveNotification(
                sentNotification,
            )
        }

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = numberOfNotifications,
            hasSentNotRegisteredToVoteCommunication = false,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
    fun `should return not registered to vote comm sent for Proxy application with not registered to vote communications sent`() {
        // Given
        val applicationId = aRandomSourceReference()

        val sentNotification = aNotificationBuilder(
            sourceReference = applicationId,
            sourceType = SourceType.PROXY,
            type = NotificationType.NOT_REGISTERED_TO_VOTE,
        )
        notificationRepository.saveNotification(
            sentNotification,
        )

        val expected = CommunicationsStatisticsResponseOAVA(
            signatureRequested = false,
            identityDocumentsRequested = false,
            bespokeCommunicationsSent = 0,
            hasSentNotRegisteredToVoteCommunication = true,
            numSignatureRequestCommsSent = 0,
            numIdentityDocumentRequestCommsSent = 0,
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
