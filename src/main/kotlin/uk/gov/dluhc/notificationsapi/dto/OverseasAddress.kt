package uk.gov.dluhc.notificationsapi.dto

data class OverseasAddress(
    val addressee: String,
    val addressLine1: String,
    val addressLine2: String?,
    val addressLine3: String?,
    val addressLine4: String?,
    val addressLine5: String?,
    val country: String,
) {

    /**
     * Map the properties to the reserved Personalisation placeholders defined in the
     * [Notify Service](https://docs.notifications.service.gov.uk/java.html#send-a-letter-arguments-personalisation-required)
     */
    fun toPersonalisationMap(): Map<String, String?> {
        return mapOf(
            "address_line_1" to addressee,
            "address_line_2" to addressLine1,
            "address_line_3" to addressLine2,
            "address_line_4" to addressLine3,
            "address_line_5" to addressLine4,
            "address_line_6" to addressLine5,
            "address_line_7" to country,
        )
    }
}
