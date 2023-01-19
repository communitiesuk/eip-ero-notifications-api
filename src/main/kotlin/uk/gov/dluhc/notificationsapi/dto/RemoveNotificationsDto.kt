package uk.gov.dluhc.notificationsapi.dto

data class RemoveNotificationsDto(
    val sourceType: SourceType,
    val sourceReference: String
)
