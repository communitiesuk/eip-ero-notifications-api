package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseBody
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseFromEmail
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseSubject
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aNotifySendEmailSuccessResponseTemplateVersion
import java.util.UUID

fun buildNotifyDetails(
    notificationId: UUID = aNotifySendEmailSuccessResponseId(),
    reference: String = aNotifySendEmailSuccessResponseReference(),
    templateId: UUID = aNotifySendEmailSuccessResponseTemplateId(),
    templateVersion: Int = aNotifySendEmailSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendEmailSuccessResponseTemplateUri(templateId),
    body: String = aNotifySendEmailSuccessResponseBody(),
    subject: String = aNotifySendEmailSuccessResponseSubject(),
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
