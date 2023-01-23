package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.CommunicationsSummary
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.TemplateType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aNotificationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anOffsetDateTime
import java.time.OffsetDateTime
import java.util.UUID

fun aCommunicationsSummaryBuilder(
    id: UUID = aNotificationId(),
    requestor: String = aRequestor(),
    channel: NotificationChannel = NotificationChannel.EMAIL,
    templateType: TemplateType = TemplateType.APPLICATION_MINUS_APPROVED,
    timestamp: OffsetDateTime = anOffsetDateTime(),
): CommunicationsSummary =
    CommunicationsSummary(
        id = id,
        channel = channel,
        requestor = requestor,
        timestamp = timestamp,
        templateType = templateType,
    )

fun aCommunicationsSummary(): CommunicationsSummary = aCommunicationsSummaryBuilder()
