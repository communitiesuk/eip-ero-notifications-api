package uk.gov.dluhc.notificationsapi.exception

/**
 * This exception is thrown when the gssCode received in a DTO is not associated with the
 * eroId that the user is authorized for (i.e. that was provided in the JWT).
 */
class GssCodeMismatchException(
    eroId: String,
    requestGssCode: String,
) : RuntimeException(
    "Request gssCode:[$requestGssCode] does not belong to eroId:[$eroId]"
)
