package uk.gov.dluhc.notificationsapi.dto

data class ContactDetailsDto(
    val localAuthorityName: String,
    val website: String,
    val phone: String,
    val email: String,
    val address: AddressDto
)

data class AddressDto(
    val street: String,
    val `property`: String?,
    val locality: String?,
    val town: String?,
    val area: String?,
    val postcode: String,
    val country: String?
)
