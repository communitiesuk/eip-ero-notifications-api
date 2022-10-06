package uk.gov.dluhc.notificationsapi.client.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.model.Content
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.Template
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseFromEmail
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateVersion
import uk.gov.service.notify.SendEmailResponse

class SendNotificationResponseDtoMapperTestDto {

    private val mapper =
        SendNotificationResponseMapperImpl()

    @Test
    fun `should map from SendEmailResponse to NotifySendEmailSuccessResponse`() {
        // Given
        val expectedNotificationId = aNotifySendEmailSuccessResponseId()
        val expectedReference = aNotifySendEmailSuccessResponseReference()
        val expectedTemplateId = aNotifySendEmailSuccessResponseTemplateId()
        val expectedTemplateVersion = aNotifySendEmailSuccessResponseTemplateVersion()
        val expectedTemplateUri: String = aNotifySendEmailSuccessResponseTemplateUri(expectedTemplateId)
        val expectedBody: String = aNotifySendEmailSuccessResponseBody()
        val expectedSubject: String = aNotifySendEmailSuccessResponseSubject()
        val expectedFromEmail = aNotifySendEmailSuccessResponseFromEmail()

        val response =
            NotifySendEmailSuccessResponse(
                id = expectedNotificationId.toString(),
                reference = expectedReference,
                template = Template(
                    id = expectedTemplateId.toString(),
                    version = expectedTemplateVersion.toString(),
                    uri = expectedTemplateUri
                ),
                content = Content(
                    body = expectedBody,
                    from_email = expectedFromEmail,
                    subject = expectedSubject
                )
            )
        val objectMapper = ObjectMapper()
        val sendEmailResponse = SendEmailResponse(objectMapper.writeValueAsString(response))

        // When
        val actual = mapper.toSendNotificationResponse(sendEmailResponse)

        // Then
        assertThat(actual.notificationId).isEqualTo(expectedNotificationId)
        assertThat(actual.reference).isEqualTo(expectedReference)
        assertThat(actual.templateId).isEqualTo(expectedTemplateId)
        assertThat(actual.templateVersion).isEqualTo(expectedTemplateVersion)
        assertThat(actual.templateUri).isEqualTo(expectedTemplateUri)
        assertThat(actual.body).isEqualTo(expectedBody)
        assertThat(actual.subject).isEqualTo(expectedSubject)
        assertThat(actual.fromEmail).isEqualTo(expectedFromEmail)
    }
}
