package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.domain.EmailNotification
import uk.gov.dluhc.notificationsapi.domain.NotificationType
import uk.gov.dluhc.notificationsapi.domain.SendNotificationResponse
import uk.gov.dluhc.notificationsapi.domain.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationPersonalisationMap
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.aSendNotificationDto
import java.time.LocalDateTime
import java.util.UUID

fun anEmailNotificationBuilder(
    id: UUID = aNotificationId(),
    gssCode: String = aGssCode(),
    requestor: String = aRequestor(),
    sourceType: SourceType = aSourceType(),
    sourceReference: String = aSourceReference(),
    toEmail: String = anEmailAddress(),
    type: NotificationType = aNotificationType(),
    personalisation: Map<String, String> = aNotificationPersonalisationMap(),
    sentAt: LocalDateTime = aLocalDateTime(),
    notifyDetails: SendNotificationResponse = aSendNotificationDto(),
): EmailNotification =
    EmailNotification(
        id = id,
        gssCode = gssCode,
        sourceReference = sourceReference,
        sourceType = sourceType,
        type = type,
        toEmail = toEmail,
        requestor = requestor,
        sentAt = sentAt,
        personalisation = personalisation,
        notifyDetails = notifyDetails,
    )

fun anEmailNotification(): EmailNotification = anEmailNotificationBuilder()
