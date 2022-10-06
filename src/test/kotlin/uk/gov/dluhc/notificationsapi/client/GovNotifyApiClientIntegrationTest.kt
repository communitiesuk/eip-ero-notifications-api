package uk.gov.dluhc.notificationsapi.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import java.util.UUID

internal class GovNotifyApiClientIntegrationTest : IntegrationTest() {

    @Nested
    inner class SendEmail {
        @Test
        fun `should send email`() {
            // Given
            val notificationType = NotificationType.APPLICATION_RECEIVED
            val emailAddress = DataFaker.faker.internet().emailAddress()
            val personalisation = mapOf<String, Any>()
            val notificationId = UUID.randomUUID()
            wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

            // When
            govNotifyApiClient.sendEmail(notificationType, emailAddress, personalisation, notificationId)

            // Then
            wireMockService.verifyNotifySendEmailCalled()
        }
    }

    @Nested
    inner class GenerateTemplatePreview {
        @Test
        fun `should generate template preview`() {
            // Given
            val templateId = UUID.randomUUID().toString()
            val response = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
            wireMockService.stubNotifyGenerateTemplatePreviewResponse(response)
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val expected = with(response) { NotifyTemplatePreviewDto(body, subject, html) }

            // When
            val actual = govNotifyApiClient.generateTemplatePreview(templateId, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            wireMockService.verifyNotifyGenerateTemplatePreview(templateId, personalisation)
        }
    }
}
