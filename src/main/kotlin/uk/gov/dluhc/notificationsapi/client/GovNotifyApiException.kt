package uk.gov.dluhc.notificationsapi.client

/**
 * Exception classes used when calling Gov Notify API is not successful.
 * Allows for communicating the error condition/state back to consuming code within this module without exposing the
 * underlying exception and technology.
 * This abstracts the consuming code from having to deal with, for example, a NotificationClientException
 */

abstract class GovNotifyApiException(message: String) : RuntimeException(message)

class GovNotifyApiNotFoundException(message: String) :
    GovNotifyApiException(message)

class GovNotifyApiBadRequestException(message: String) :
    GovNotifyApiException(message)

class GovNotifyApiGeneralException(message: String) :
    GovNotifyApiException(message)
