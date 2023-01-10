package uk.gov.dluhc.notificationsapi.dto

data class PostalAddress(
    val addressee: String,
    val property: String?,
    val street: String,
    val town: String?,
    val area: String?,
    val locality: String?,
    val postcode: String,
) {

    /**
     * Map the properties to the reserved Personalisation placeholders defined in the
     * [Notify Service](https://docs.notifications.service.gov.uk/java.html#send-a-letter-arguments-personalisation-required)
     */
    fun toPersonalisationMap(): Map<String, String?> {
        return mapOf(
            "address_line_1" to addressee,
            "address_line_2" to property,
            "address_line_3" to street,
            "address_line_4" to town,
            "address_line_5" to area,
            "address_line_6" to locality,
            "address_line_7" to postcode,
        )
    }
}
