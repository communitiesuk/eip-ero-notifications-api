package uk.gov.dluhc.notificationsapi.testsupport

fun String.replaceSpacesWith(replacement: String): String = replace(Regex("\\s+"), replacement)
