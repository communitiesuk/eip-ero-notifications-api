package uk.gov.dluhc.notificationsapi.database

import java.util.UUID

abstract class NotificationRepositoryException(message: String) : RuntimeException(message)

class NotificationNotFoundException(id: UUID) :
    NotificationRepositoryException("Notification item not found for id: $id")
