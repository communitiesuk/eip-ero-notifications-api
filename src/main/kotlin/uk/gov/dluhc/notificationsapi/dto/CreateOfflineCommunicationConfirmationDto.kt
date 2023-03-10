package uk.gov.dluhc.notificationsapi.dto

import java.time.LocalDateTime

data class CreateOfflineCommunicationConfirmationDto(
    val eroId: String,
    val sourceReference: String,
    val sourceType: SourceType,
    val gssCode: String,
    val reason: OfflineCommunicationReasonDto,
    val channel: OfflineCommunicationChannelDto,
    val requestor: String,
    val sentAt: LocalDateTime,
)

enum class OfflineCommunicationReasonDto {
    APPLICATION_REJECTED,
    PHOTO_REJECTED,
    DOCUMENT_REJECTED,
}

enum class OfflineCommunicationChannelDto {
    EMAIL,
    LETTER,
    TELEPHONE,
}
