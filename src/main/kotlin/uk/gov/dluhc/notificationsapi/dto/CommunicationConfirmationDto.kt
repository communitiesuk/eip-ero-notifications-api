package uk.gov.dluhc.notificationsapi.dto

import java.time.LocalDateTime
import java.util.UUID

data class CommunicationConfirmationDto(
    val id: UUID? = null,
    val eroId: String? = null,
    val sourceReference: String,
    val sourceType: SourceType,
    val gssCode: String,
    val reason: CommunicationConfirmationReasonDto,
    val channel: CommunicationConfirmationChannelDto,
    val requestor: String,
    val sentAt: LocalDateTime,
)

enum class CommunicationConfirmationReasonDto {
    APPLICATION_REJECTED,
    PHOTO_REJECTED,
    DOCUMENT_REJECTED,
}

enum class CommunicationConfirmationChannelDto {
    EMAIL,
    LETTER,
    TELEPHONE,
}
