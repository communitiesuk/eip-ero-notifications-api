package uk.gov.dluhc.notificationsapi.exception

/**
 * Thrown if functionality is not available for a particular SourceType
 */
class InvalidSourceTypeException(message: String) : RuntimeException(message)
