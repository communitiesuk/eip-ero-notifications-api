package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationAudit
import uk.gov.dluhc.notificationsapi.database.entity.NotificationAuditTemplateDetails
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationAuditId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import java.time.LocalDateTime
import java.util.UUID

fun buildNotificationAudit(
    id: UUID = aNotificationAuditId(),
    sourceReference: String = aSourceReference(),
    gssCode: String = aGssCode(),
    sourceType: SourceType = anEntitySourceType(),
    channel: Channel = anEntityChannel(),
    requestor: String = aRequestor(),
    sentAt: LocalDateTime = aLocalDateTime(),
    templateDetails: NotificationAuditTemplateDetails = aNotificationAuditTemplateDetails(),
): NotificationAudit =
    NotificationAudit(
        id = id,
        sourceReference = sourceReference,
        gssCode = gssCode,
        sourceType = sourceType,
        channel = channel,
        requestor = requestor,
        sentAt = sentAt,
        templateDetails = templateDetails,
    )

fun aNotificationAudit() = buildNotificationAudit()
