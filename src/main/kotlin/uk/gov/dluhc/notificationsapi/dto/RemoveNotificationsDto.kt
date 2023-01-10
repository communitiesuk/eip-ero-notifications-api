package uk.gov.dluhc.notificationsapi.dto

data class RemoveNotificationsDto(
    val gssCode: String,
    val sourceType: SourceType,
    val sourceReference: String
)
