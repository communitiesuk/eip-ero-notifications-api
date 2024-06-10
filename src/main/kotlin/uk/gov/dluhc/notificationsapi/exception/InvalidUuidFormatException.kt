package uk.gov.dluhc.notificationsapi.exception

/**
 * This exception is thrown when an id is received that cannot be parsed into a UUID.
 */
class InvalidUuidFormatException(
    id: String,
) : RuntimeException(
    "Id:[$id] is not a valid UUID",
)
