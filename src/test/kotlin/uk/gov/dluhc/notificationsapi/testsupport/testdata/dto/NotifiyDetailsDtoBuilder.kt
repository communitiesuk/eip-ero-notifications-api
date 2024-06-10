package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotifyDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import java.util.UUID

fun aNotifyDetailsDtoBuilder(
    notificationId: UUID = aNotificationId(),
    reference: String = aNotifySendSuccessResponseReference(),
    templateId: UUID = aTemplateId(),
    templateVersion: Int = aNotifySendSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendSuccessResponseTemplateUri(templateId),
    body: String = aNotifySendSuccessResponseBody(),
    subject: String = aNotifySendSuccessResponseSubject(),
    fromEmail: String? = aNotifySendEmailSuccessResponseFromEmail(),
): NotifyDetailsDto =
    NotifyDetailsDto(
        notificationId = notificationId,
        reference = reference,
        templateId = templateId,
        templateVersion = templateVersion,
        templateUri = templateUri,
        body = body,
        subject = subject,
        fromEmail = fromEmail,
    )

fun aNotifyDetailsDto(): NotifyDetailsDto = aNotifyDetailsDtoBuilder()
