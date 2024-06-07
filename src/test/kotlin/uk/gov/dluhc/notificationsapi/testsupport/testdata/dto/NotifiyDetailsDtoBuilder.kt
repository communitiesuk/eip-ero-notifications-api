package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotifyDetailsDto
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime
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
