package uk.gov.dluhc.notificationsapi.dto

import uk.gov.dluhc.notificationsapi.utils.getSafeValue

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
        personalisation["eroAddressLine1"] = property.getSafeValue()
        personalisation["eroAddressLine2"] = street
        personalisation["eroAddressLine3"] = town.getSafeValue()
        personalisation["eroAddressLine4"] = area.getSafeValue()
        personalisation["eroAddressLine5"] = locality.getSafeValue()
        personalisation["eroPostcode"] = postcode
    }
}
