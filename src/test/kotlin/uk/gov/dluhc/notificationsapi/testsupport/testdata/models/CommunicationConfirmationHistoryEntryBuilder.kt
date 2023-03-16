package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.CommunicationConfirmationHistoryEntry
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aCommunicationConfirmationId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aRequestor
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anOffsetDateTime
import java.time.OffsetDateTime
import java.util.UUID

fun aCommunicationConfirmationHistoryEntryBuilder(
    id: UUID = aCommunicationConfirmationId(),
    gssCode: String,
    reason: OfflineCommunicationReason = OfflineCommunicationReason.APPLICATION_MINUS_REJECTED,
    channel: OfflineCommunicationChannel = OfflineCommunicationChannel.EMAIL,
    requestor: String = aRequestor(),
    timestamp: OffsetDateTime = anOffsetDateTime(),
): CommunicationConfirmationHistoryEntry =
    CommunicationConfirmationHistoryEntry(
        id = id,
        gssCode = gssCode,
        reason = reason,
        channel = channel,
        requestor = requestor,
        timestamp = timestamp,
    )
