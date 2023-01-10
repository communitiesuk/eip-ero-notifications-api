package uk.gov.dluhc.notificationsapi.client.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.dluhc.notificationsapi.testsupport.model.Content
import uk.gov.dluhc.notificationsapi.testsupport.model.LetterContent
import uk.gov.dluhc.notificationsapi.testsupport.model.LetterTemplate
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendEmailSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.NotifySendLetterSuccessResponse
import uk.gov.dluhc.notificationsapi.testsupport.model.Template
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseFromEmail
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendLetterSuccessResponsePostage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateVersion
import uk.gov.service.notify.SendEmailResponse
import uk.gov.service.notify.SendLetterResponse

class SendNotificationResponseDtoMapperTestDto {

    private val mapper =
        SendNotificationResponseMapperImpl()

    @Test
    fun `should map from SendEmailResponse to NotifySendEmailSuccessResponse`() {
        // Given
        val expectedNotificationId = aNotifySendSuccessResponseId()
        val expectedReference = aNotifySendSuccessResponseReference()
        val expectedTemplateId = aNotifySendSuccessResponseTemplateId()
        val expectedTemplateVersion = aNotifySendSuccessResponseTemplateVersion()
        val expectedTemplateUri: String = aNotifySendSuccessResponseTemplateUri(expectedTemplateId)
        val expectedBody: String = aNotifySendSuccessResponseBody()
        val expectedSubject: String = aNotifySendSuccessResponseSubject()
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

    @Test
    fun `should map from SendLetterResponse to NotifySendLetterSuccessResponse`() {
        // Given
        val expectedNotificationId = aNotifySendSuccessResponseId()
        val expectedReference = aNotifySendSuccessResponseReference()
        val expectedTemplateId = aNotifySendSuccessResponseTemplateId()
        val expectedTemplateVersion = aNotifySendSuccessResponseTemplateVersion()
        val expectedTemplateUri: String = aNotifySendSuccessResponseTemplateUri(expectedTemplateId)
        val expectedBody: String = aNotifySendSuccessResponseBody()
        val expectedSubject: String = aNotifySendSuccessResponseSubject()
        val expectedPostage = aNotifySendLetterSuccessResponsePostage()

        val response =
            NotifySendLetterSuccessResponse(
                id = expectedNotificationId.toString(),
                reference = expectedReference,
                postage = expectedPostage,
                template = LetterTemplate(
                    id = expectedTemplateId.toString(),
                    version = expectedTemplateVersion.toString(),
                    uri = expectedTemplateUri
                ),
                content = LetterContent(
                    body = expectedBody,
                    subject = expectedSubject
                )
            )
        val objectMapper = ObjectMapper()
        val sendLetterResponse = SendLetterResponse(objectMapper.writeValueAsString(response))

        // When
        val actual = mapper.toSendNotificationResponse(sendLetterResponse)

        // Then
        assertThat(actual.notificationId).isEqualTo(expectedNotificationId)
        assertThat(actual.reference).isEqualTo(expectedReference)
        assertThat(actual.templateId).isEqualTo(expectedTemplateId)
        assertThat(actual.templateVersion).isEqualTo(expectedTemplateVersion)
        assertThat(actual.templateUri).isEqualTo(expectedTemplateUri)
        assertThat(actual.body).isEqualTo(expectedBody)
        assertThat(actual.subject).isEqualTo(expectedSubject)
        assertThat(actual.fromEmail).isNull()
    }
}
