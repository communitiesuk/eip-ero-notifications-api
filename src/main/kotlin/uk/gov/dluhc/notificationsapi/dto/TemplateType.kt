package uk.gov.dluhc.notificationsapi.dto

/**
 * An enum containing types of notification templates
 */
enum class TemplateType {
    APPLICATION_RECEIVED,
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    PHOTO_RESUBMISSION,
    ID_DOCUMENT_RESUBMISSION,
}
