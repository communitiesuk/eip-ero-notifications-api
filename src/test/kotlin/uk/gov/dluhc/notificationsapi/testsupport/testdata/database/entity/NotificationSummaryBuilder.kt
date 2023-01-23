package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.NotificationSummary
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import java.time.LocalDateTime
import java.util.UUID

fun aNotificationSummaryBuilder(
    id: UUID = aNotificationId(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = anEntitySourceType(),
    sourceReference: String = aSourceReference(),
    type: NotificationType = anEntityNotificationType(),
    channel: Channel = anEntityChannel(),
    sentAt: LocalDateTime = aLocalDateTime(),
): NotificationSummary =
    NotificationSummary(
        id = id,
        gssCode = gssCode,
        sourceReference = sourceReference,
        sourceType = sourceType,
        type = type,
        channel = channel,
        requestor = requestor,
        sentAt = sentAt,
    )

fun aNotificationSummary(): NotificationSummary = aNotificationSummaryBuilder()
