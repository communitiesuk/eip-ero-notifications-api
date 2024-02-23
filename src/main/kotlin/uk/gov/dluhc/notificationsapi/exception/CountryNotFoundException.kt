package uk.gov.dluhc.notificationsapi.exception

/**
 * This exception is thrown when the country is not found for an overseas document template
 */
class CountryNotFoundException(
) : RuntimeException(
    "Country is required to process a template for overseas"
)
