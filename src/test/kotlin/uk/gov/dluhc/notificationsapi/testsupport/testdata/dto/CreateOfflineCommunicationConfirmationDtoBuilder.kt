package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationReasonDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidKnownEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime

fun aCommunicationConfirmationDtoBuilder(
    eroId: String = aValidKnownEroId(),
    sourceReference: String = aSourceReference(),
    sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT,
    gssCode: String = aGssCode(),
    reason: CommunicationConfirmationReasonDto = CommunicationConfirmationReasonDto.APPLICATION_REJECTED,
    channel: CommunicationConfirmationChannelDto = CommunicationConfirmationChannelDto.LETTER,
    requestor: String = anEmailAddress(),
    sentAt: LocalDateTime = aLocalDateTime(),
) = CommunicationConfirmationDto(
    eroId = eroId,
    gssCode = gssCode,
    sourceReference = sourceReference,
    sourceType = sourceType,
    reason = reason,
    channel = channel,
    requestor = requestor,
    sentAt = sentAt,
)
