package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CreateOfflineCommunicationConfirmationDto
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationReasonDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aGssCode
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aLocalDateTime
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aSourceReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidKnownEroId
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import java.time.LocalDateTime

fun aCreateOfflineCommunicationConfirmationDtoBuilder(
    eroId: String = aValidKnownEroId(),
    sourceReference: String = aSourceReference(),
    sourceType: SourceType = SourceType.ANONYMOUS_ELECTOR_DOCUMENT,
    gssCode: String = aGssCode(),
    reason: OfflineCommunicationReasonDto = OfflineCommunicationReasonDto.APPLICATION_REJECTED,
    channel: OfflineCommunicationChannelDto = OfflineCommunicationChannelDto.LETTER,
    requestor: String = anEmailAddress(),
    sentAt: LocalDateTime = aLocalDateTime(),
) = CreateOfflineCommunicationConfirmationDto(
    eroId = eroId,
    gssCode = gssCode,
    sourceReference = sourceReference,
    sourceType = sourceType,
    reason = reason,
    channel = channel,
    requestor = requestor,
    sentAt = sentAt,
)
