package uk.gov.dluhc.notificationsapi.testsupport.testdata.database.entity

import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmation
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason
import uk.gov.dluhc.notificationsapi.database.entity.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime
import java.util.UUID

fun aCommunicationConfirmationBuilder(
    id: UUID = UUID.randomUUID(),
    sourceReference: String = aSourceReference(),
    sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT,
    gssCode: String = aGssCode(),
    reason: CommunicationConfirmationReason = CommunicationConfirmationReason.APPLICATION_REJECTED,
    channel: CommunicationConfirmationChannel = CommunicationConfirmationChannel.LETTER,
    requestor: String = anEmailAddress(),
    sentAt: LocalDateTime = aLocalDateTime(),
) = CommunicationConfirmation(
    id = id,
    gssCode = gssCode,
    sourceReference = sourceReference,
    sourceType = sourceType,
    reason = reason,
    channel = channel,
    requestor = requestor,
    sentAt = sentAt,
)
