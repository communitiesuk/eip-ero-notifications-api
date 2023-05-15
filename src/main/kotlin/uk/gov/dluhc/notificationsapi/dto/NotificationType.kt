package uk.gov.dluhc.notificationsapi.dto

/**
 * Types of notification handled by the service.
 */
enum class NotificationType {
    APPLICATION_RECEIVED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    PHOTO_RESUBMISSION,
    ID_DOCUMENT_RESUBMISSION,
    ID_DOCUMENT_REQUIRED,
    REJECTED_DOCUMENT,
    REJECTED_SIGNATURE
}
