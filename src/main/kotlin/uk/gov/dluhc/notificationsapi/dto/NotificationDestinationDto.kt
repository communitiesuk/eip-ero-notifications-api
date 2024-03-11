package uk.gov.dluhc.notificationsapi.dto

data class NotificationDestinationDto(
    val emailAddress: String?,
    val postalAddress: PostalAddress?,
    val overseasAddress: OverseasAddress?
)
