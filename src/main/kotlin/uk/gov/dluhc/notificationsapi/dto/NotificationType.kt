package uk.gov.dluhc.notificationsapi.dto

/**
 * Types of notification handled by the service.
 *
 * PHOTO_RESUBMISSION_WITH_REASONS is an implementation detail, and is not a "business" notification type - ie. the business
 * talk about "the photo resubmission notification", and do not differentiate between with and without reasons.
 * It is defined here so that it can be mapped to a gov.uk notify templateId (ie. it's an implementation detail), but will be
 * saved to the database (and returned through the REST API) as PHOTO_RESUBMISSION
 *
 * ID_DOCUMENT_RESUBMISSION_WITH_REASONS is an implementation detail, and is not a "business" notification type - ie. the business
 * talk about "the ID document resubmission notification", and do not differentiate between with and without reasons.
 * It is defined here so that it can be mapped to a gov.uk notify templateId (ie. it's an implementation detail), but will be
 * saved to the database (and returned through the REST API) as ID_DOCUMENT_RESUBMISSION
 */
enum class NotificationType {
    APPLICATION_RECEIVED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    PHOTO_RESUBMISSION,
    PHOTO_RESUBMISSION_WITH_REASONS,
    ID_DOCUMENT_RESUBMISSION,
    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
    ID_DOCUMENT_REQUIRED,
    REJECTED_DOCUMENT,
    REJECTED_SIGNATURE
}
