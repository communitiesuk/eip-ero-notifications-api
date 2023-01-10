package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendEmailSuccessResponseFromEmail
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateVersion
import java.util.UUID

fun buildNotifyDetails(
    notificationId: UUID = aNotifySendSuccessResponseId(),
    reference: String = aNotifySendSuccessResponseReference(),
    templateId: UUID = aNotifySendSuccessResponseTemplateId(),
    templateVersion: Int = aNotifySendSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendSuccessResponseTemplateUri(templateId),
    body: String = aNotifySendSuccessResponseBody(),
    subject: String = aNotifySendSuccessResponseSubject(),
    fromEmail: String? = aNotifySendEmailSuccessResponseFromEmail(),
): NotifyDetails =
    NotifyDetails(
        notificationId = notificationId,
        reference = reference,
        templateId = templateId,
        templateVersion = templateVersion,
        templateUri = templateUri,
        body = body,
        subject = subject,
        fromEmail = fromEmail,
    )

fun aNotifyDetails() = buildNotifyDetails()

fun aNotifyDetails(dto: SendNotificationResponseDto): NotifyDetails =
    NotifyDetails(
        notificationId = dto.notificationId,
        reference = dto.reference,
        templateId = dto.templateId,
        templateVersion = dto.templateVersion,
        templateUri = dto.templateUri,
        body = dto.body,
        subject = dto.subject,
        fromEmail = dto.fromEmail,
    )
