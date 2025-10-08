package uk.gov.dluhc.notificationsapi.utils

fun String?.getSafeValue(): String = this?.ifBlank { "" } ?: ""
