package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.Channel
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRandomNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime
import java.util.UUID

fun aNotificationBuilder(
    id: UUID = aRandomNotificationId(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = anEntitySourceType(),
    sourceReference: String = aSourceReference(),
    toEmail: String = anEmailAddress(),
    type: NotificationType = anEntityNotificationType(),
    channel: Channel = anEntityChannel(),
    personalisation: Map<String, String> = aNotificationPersonalisationMap(),
    sentAt: LocalDateTime = aLocalDateTime(),
    notifyDetails: NotifyDetails = aNotifyDetails(),
): Notification =
    Notification(
        id = id,
        gssCode = gssCode,
        sourceReference = sourceReference,
        sourceType = sourceType,
        type = type,
        channel = channel,
        toEmail = toEmail,
        requestor = requestor,
        sentAt = sentAt,
        personalisation = personalisation,
        notifyDetails = notifyDetails,
    )

fun aNotification(): Notification = aNotificationBuilder()

fun anEntityNotificationType() = NotificationType.APPLICATION_APPROVED
fun anEntitySourceType() = SourceType.VOTER_CARD
fun anEntityChannel() = Channel.EMAIL
