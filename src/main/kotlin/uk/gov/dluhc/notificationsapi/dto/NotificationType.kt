package uk.gov.dluhc.notificationsapi.dto

/**
 * Types of notification handled by the service.
 *
 * The notification types
 * - PHOTO_RESUBMISSION_WITH_REASONS
 * - ID_DOCUMENT_RESUBMISSION_WITH_REASONS
 * - REJECTED_SIGNATURE_WITH_REASONS
 * - NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST
 * are implementation details, not "business" notification types, and do not differentiate between with vs without
 * reasons, or ordinary vs restricted document list electors. They are defined here so that they can be mapped to a
 * different gov.uk notify templateId (ie. it's an implementation detail), but will be saved to the database (and
 * returned through the REST API) as PHOTO_RESUBMISSION / ID_DOCUMENT_RESUBMISSION / REJECTED_SIGNATURE /
 * NINO_NOT_MATCHED
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
    REJECTED_SIGNATURE,
    REJECTED_SIGNATURE_WITH_REASONS,
    REQUESTED_SIGNATURE,
    NINO_NOT_MATCHED,
    NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST,
    REJECTED_PARENT_GUARDIAN,
    REJECTED_PREVIOUS_ADDRESS,
    PARENT_GUARDIAN_PROOF_REQUIRED,
    PREVIOUS_ADDRESS_DOCUMENT_REQUIRED,
    BESPOKE_COMM,
    NOT_REGISTERED_TO_VOTE,
    SIGNATURE_RESUBMISSION,
    SIGNATURE_RESUBMISSION_WITH_REASONS,
}

enum class NotificationCategory {
    SIGNATURE_REQUESTED,
    IDENTITY_DOCUMENTS_REQUESTED,
    BESPOKE_COMMUNICATION_SENT,
    NOT_REGISTERED_TO_VOTE_COMMUNICATION,
    PHOTO_REQUESTED,
}

val notificationCategories = mapOf(
    NotificationCategory.SIGNATURE_REQUESTED to listOf(NotificationType.REJECTED_SIGNATURE, NotificationType.REJECTED_SIGNATURE_WITH_REASONS, NotificationType.REQUESTED_SIGNATURE, NotificationType.SIGNATURE_RESUBMISSION, NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS),
    NotificationCategory.IDENTITY_DOCUMENTS_REQUESTED to listOf(NotificationType.ID_DOCUMENT_REQUIRED, NotificationType.ID_DOCUMENT_RESUBMISSION, NotificationType.NINO_NOT_MATCHED),
    NotificationCategory.BESPOKE_COMMUNICATION_SENT to listOf(NotificationType.BESPOKE_COMM),
    NotificationCategory.NOT_REGISTERED_TO_VOTE_COMMUNICATION to listOf(NotificationType.NOT_REGISTERED_TO_VOTE),
    NotificationCategory.PHOTO_REQUESTED to listOf(NotificationType.PHOTO_RESUBMISSION),
)
