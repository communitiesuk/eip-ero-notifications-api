package uk.gov.dluhc.notificationsapi.client

import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse

internal class GovNotifyApiClientTest : IntegrationTest() {

    @Test
    fun `should send email`() {
        // Given
        wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

        // When
        govNotifyApiClient.sendEmail()

        // Then
        wireMockService.verifyNotifySendEmailCalled()
    }
}
