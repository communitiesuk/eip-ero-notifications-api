package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.NotificationAuditTemplateDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateUri
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.aNotifySendSuccessResponseTemplateVersion
import java.util.UUID

fun buildNotificationAuditTemplateDetails(
    templateId: UUID = aNotifySendSuccessResponseTemplateId(),
    templateVersion: Int = aNotifySendSuccessResponseTemplateVersion(),
    templateUri: String = aNotifySendSuccessResponseTemplateUri(templateId),
): NotificationAuditTemplateDetails =
    NotificationAuditTemplateDetails(
        templateId = templateId,
        templateVersion = templateVersion,
        templateUri = templateUri,
    )

fun aNotificationAuditTemplateDetails() = buildNotificationAuditTemplateDetails()
