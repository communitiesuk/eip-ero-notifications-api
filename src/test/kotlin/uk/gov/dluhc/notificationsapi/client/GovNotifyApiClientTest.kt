package uk.gov.dluhc.notificationsapi.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.model.NotificationType
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.Template
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.service.notify.NotificationClient
import uk.gov.service.notify.SendEmailResponse
import uk.gov.service.notify.TemplatePreview
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockitoExtension::class)
internal class GovNotifyApiClientTest {
    @InjectMocks
    private lateinit var govNotifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var notificationClient: NotificationClient

    @Nested
    inner class SendEmail {
        @ParameterizedTest
        @ValueSource(strings = ["html body"])
        @NullSource
        fun `should generate template preview given existing html`(html: String?) {
            // Given
            val notificationType = NotificationType.APPLICATION_RECEIVED
            val emailAddress = DataFaker.faker.internet().emailAddress()
            val reference = UUID.randomUUID()
            val templateId = UUID.randomUUID().toString()
            given(notificationTemplateMapper.fromNotificationType(any())).willReturn(templateId)
            val response =
                NotifySendEmailSuccessResponse(template = Template(id = templateId), reference = reference.toString())
            val objectMapper = ObjectMapper()
            val sendEmailResponse = SendEmailResponse(objectMapper.writeValueAsString(response))
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
                "date" to LocalDate.now()
            )
            given(notificationClient.sendEmail(any(), any(), any(), any())).willReturn(sendEmailResponse)

            // When
            govNotifyApiClient.sendEmail(notificationType, emailAddress, personalisation, reference)

            // Then
            verify(notificationTemplateMapper).fromNotificationType(notificationType)
            verify(notificationClient).sendEmail(templateId, emailAddress, personalisation, reference.toString())
        }
    }

    @Nested
    inner class GenerateTemplatePreview {
        @ParameterizedTest
        @ValueSource(strings = ["html body"])
        @NullSource
        fun `should generate template preview given existing html`(html: String?) {
            // Given
            val objectMapper = ObjectMapper()
            val templateId = UUID.randomUUID().toString()
            val response = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId, html = html)
            val previewResponse = TemplatePreview(objectMapper.writeValueAsString(response))
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
                "date" to LocalDate.now()
            )
            given(notificationClient.generateTemplatePreview(any(), any())).willReturn(previewResponse)
            val expected = NotifyTemplatePreviewDto(response.body, html)

            // When
            val actual = govNotifyApiClient.generateTemplatePreview(templateId, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(notificationClient).generateTemplatePreview(templateId, personalisation)
        }
    }
}
