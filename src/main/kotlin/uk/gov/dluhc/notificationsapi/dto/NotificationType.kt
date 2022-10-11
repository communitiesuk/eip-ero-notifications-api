package uk.gov.dluhc.notificationsapi.dto

/**
 * Types of notification handled by the service.
 */
enum class NotificationType {
    APPLICATION_RECEIVED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    PHOTO_RESUBMISSION,
}
