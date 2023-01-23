package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationSummaryDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import java.time.LocalDateTime
import java.util.UUID

fun aNotificationSummaryDtoBuilder(
    id: UUID = aNotificationId(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = SourceType.VOTER_CARD,
    sourceReference: String = aSourceReference(),
    type: NotificationType = NotificationType.APPLICATION_APPROVED,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    sentAt: LocalDateTime = aLocalDateTime(),
): NotificationSummaryDto =
    NotificationSummaryDto(
        id = id,
        gssCode = gssCode,
        sourceReference = sourceReference,
        sourceType = sourceType,
        type = type,
        channel = channel,
        requestor = requestor,
        sentAt = sentAt,
    )

fun aNotificationSummaryDto(): NotificationSummaryDto = aNotificationSummaryDtoBuilder()
