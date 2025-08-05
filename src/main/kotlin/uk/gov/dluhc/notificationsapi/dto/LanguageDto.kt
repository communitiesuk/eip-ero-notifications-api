package uk.gov.dluhc.notificationsapi.dto

import java.util.Locale

enum class LanguageDto(val value: String, val locale: Locale) {
    WELSH(
        "cy",
        Locale.forLanguageTag("cy"),
    ),
    ENGLISH("en", Locale.ENGLISH),
}
