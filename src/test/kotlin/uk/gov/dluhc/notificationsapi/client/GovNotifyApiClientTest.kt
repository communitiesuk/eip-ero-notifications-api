package uk.gov.dluhc.notificationsapi.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.test.util.ReflectionTestUtils.setField
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.client.mapper.SendNotificationResponseMapper
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifyGenerateTemplatePreviewSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildSendNotificationDto
import uk.gov.service.notify.NotificationClient
import uk.gov.service.notify.NotificationClientException
import uk.gov.service.notify.SendEmailResponse
import uk.gov.service.notify.TemplatePreview
import java.util.UUID

@ExtendWith(MockitoExtension::class)
internal class GovNotifyApiClientTest {
    @InjectMocks
    private lateinit var govNotifyApiClient: GovNotifyApiClient

    @Mock
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Mock
    private lateinit var sendNotificationResponseMapper: SendNotificationResponseMapper

    @Mock
    private lateinit var notificationClient: NotificationClient

    @Nested
    inner class SendEmail {
        @Test
        fun `should send email`() {
            // Given
            val notificationType = aNotificationType()
            val emailAddress = anEmailAddress()
            val notificationId = aNotificationId()
            val templateId = aTemplateId()
            given(notificationTemplateMapper.fromNotificationType(any())).willReturn(templateId.toString())
            val response = NotifySendEmailSuccessResponse()
            val objectMapper = ObjectMapper()
            val sendEmailResponse = SendEmailResponse(objectMapper.writeValueAsString(response))
            val personalisation = aNotificationPersonalisationMap()
            given(notificationClient.sendEmail(any(), any(), any(), any())).willReturn(sendEmailResponse)
            val sendNotificationDto = buildSendNotificationDto()
            given(sendNotificationResponseMapper.toSendNotificationResponse(any())).willReturn(sendNotificationDto)

            // When
            val actual = govNotifyApiClient.sendEmail(notificationType, emailAddress, personalisation, notificationId)

            // Then
            verify(notificationTemplateMapper).fromNotificationType(notificationType)
            verify(notificationClient).sendEmail(templateId.toString(), emailAddress, personalisation, notificationId.toString())
            verify(sendNotificationResponseMapper).toSendNotificationResponse(sendEmailResponse)
            assertThat(actual).isSameAs(sendNotificationDto)
        }
    }

    @Nested
    inner class GenerateTemplatePreview {
        @ParameterizedTest
        @CsvSource(
            value = [
                ",",
                ",html body",
                "subject,",
                "subject, html body",
            ]
        )
        fun `should generate template preview given existing html`(subject: String?, html: String?) {
            // Given
            val objectMapper = ObjectMapper()
            val templateId = UUID.randomUUID().toString()
            val response = NotifyGenerateTemplatePreviewSuccessResponse(id = templateId, subject = subject, html = html)
            val previewResponse = TemplatePreview(objectMapper.writeValueAsString(response))
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            given(notificationClient.generateTemplatePreview(any(), any())).willReturn(previewResponse)
            val expected = NotifyTemplatePreviewDto(response.body, response.subject, html)

            // When
            val actual = govNotifyApiClient.generateTemplatePreview(templateId, personalisation)

            // Then
            assertThat(actual).isEqualTo(expected)
            verify(notificationClient).generateTemplatePreview(templateId, personalisation)
        }

        @Test
        fun `should throw GovNotifyApiNotFoundException given client throws exception with 404 http result`() {
            // Given
            val templateId = UUID.randomUUID().toString()
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val exceptionMessage = "No result found"
            val exception = NotificationClientException(exceptionMessage)
            setField(exception, "httpResult", 404)
            given(notificationClient.generateTemplatePreview(any(), any())).willThrow(exception)

            // When
            val ex = Assertions.catchThrowableOfType(
                { govNotifyApiClient.generateTemplatePreview(templateId, personalisation) },
                GovNotifyApiNotFoundException::class.java
            )

            // Then
            assertThat(ex.message).isEqualTo(exceptionMessage)
        }

        @Test
        fun `should throw GovNotifyApiBadRequestException given client throws exception with 400 http result`() {
            // Given
            val templateId = UUID.randomUUID().toString()
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val exceptionMessage = "No result found"
            val exception = NotificationClientException(exceptionMessage)
            setField(exception, "httpResult", 400)
            given(notificationClient.generateTemplatePreview(any(), any())).willThrow(exception)

            // When
            val ex = Assertions.catchThrowableOfType(
                { govNotifyApiClient.generateTemplatePreview(templateId, personalisation) },
                GovNotifyApiBadRequestException::class.java
            )

            // Then
            assertThat(ex.message).isEqualTo(exceptionMessage)
        }

        @Test
        fun `should throw GovNotifyApiGeneralException given client throws exception with http result other than 400 and 404`() {
            // Given
            val templateId = UUID.randomUUID().toString()
            val personalisation = mapOf(
                "subject_param" to "test subject",
                "name_param" to "John",
                "custom_title" to "Resubmitting photo",
            )
            val exceptionMessage = "No result found"
            val exception = NotificationClientException(exceptionMessage)
            setField(exception, "httpResult", 500)
            given(notificationClient.generateTemplatePreview(any(), any())).willThrow(exception)

            // When
            val ex = Assertions.catchThrowableOfType(
                { govNotifyApiClient.generateTemplatePreview(templateId, personalisation) },
                GovNotifyApiGeneralException::class.java
            )

            // Then
            assertThat(ex.message).isEqualTo(exceptionMessage)
        }
    }
}
