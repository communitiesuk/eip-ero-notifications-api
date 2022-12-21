package uk.gov.dluhc.notificationsapi.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.config.IntegrationTest
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aTemplateId

internal class GovNotifyApiClientIntegrationTest : IntegrationTest() {

    @Nested
    inner class SendEmail {
        @Test
        fun `should send email`() {
            // Given
            val templateId = aTemplateId().toString()
            val emailAddress = anEmailAddress()
            val personalisation = aNotificationPersonalisationMap()
            val notificationId = aNotificationId()
            wireMockService.stubNotifySendEmailResponse(NotifySendEmailSuccessResponse())

            // When
            govNotifyApiClient.sendEmail(templateId, emailAddress, personalisation, notificationId)

            // Then
            wireMockService.verifyNotifySendEmailCalled()
        }
    }

    @Nested
    inner class GenerateTemplatePreview {
        @Test
        fun `should generate template preview`() {
            // Given
            val templateId = aTemplateId().toString()
            val response = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId)
            wireMockService.stubNotifyGenerateTemplatePreviewSuccessResponse(response)
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
