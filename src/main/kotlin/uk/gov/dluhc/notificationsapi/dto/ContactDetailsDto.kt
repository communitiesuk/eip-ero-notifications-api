package uk.gov.dluhc.notificationsapi.dto

data class ContactDetailsDto(
    val localAuthorityName: String,
    val website: String,
    val phone: String,
    val email: String,
    val address: AddressDto,
)

data class AddressDto(
    val street: String,
    val `property`: String?,
    val locality: String?,
    val town: String?,
    val area: String?,
    val postcode: String,
)

fun ContactDetailsDto.mapToPersonalisation(personalisation: MutableMap<String, String>) {
    personalisation["LAName"] = localAuthorityName
    personalisation["eroPhone"] = phone
    personalisation["eroWebsite"] = website
    personalisation["eroEmail"] = email
    with(address) {
        personalisation["eroAddressLine1"] = getSafeValue(property)
        personalisation["eroAddressLine2"] = street
        personalisation["eroAddressLine3"] = getSafeValue(town)
        personalisation["eroAddressLine4"] = getSafeValue(area)
        personalisation["eroAddressLine5"] = getSafeValue(locality)
        personalisation["eroPostcode"] = postcode
    }
}

fun getSafeValue(input: String?): String = input?.ifBlank { "" } ?: ""
