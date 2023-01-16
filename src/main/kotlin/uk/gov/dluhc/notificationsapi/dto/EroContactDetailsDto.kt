package uk.gov.dluhc.notificationsapi.dto

data class EroContactDetailsDto(
    val name: String,
    val emailAddress: String,
    val website: String,
    val phoneNumber: String,
    var address: AddressDto,
)
