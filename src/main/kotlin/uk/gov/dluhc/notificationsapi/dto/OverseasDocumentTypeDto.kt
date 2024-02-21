package uk.gov.dluhc.notificationsapi.dto

enum class OverseasDocumentTypeDto(val value: String) {
    IDENTITY("identity"),
    PARENT_GUARDIAN("parent-guardian"),
    QUALIFYING_ADDRESS("qualifying-address")
}