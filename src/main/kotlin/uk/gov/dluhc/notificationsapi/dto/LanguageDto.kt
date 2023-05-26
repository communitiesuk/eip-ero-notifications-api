package uk.gov.dluhc.notificationsapi.dto

import java.util.Locale

enum class LanguageDto(val value: String, val locale: Locale, val languageName: String) {
    WELSH("cy", Locale("cy", "WALES"), "Welsh"),
    ENGLISH("en", Locale.ENGLISH, "English"),
}
