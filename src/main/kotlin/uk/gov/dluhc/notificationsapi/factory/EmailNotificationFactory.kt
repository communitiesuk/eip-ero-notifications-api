package uk.gov.dluhc.notificationsapi.factory

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.domain.EmailNotification
import uk.gov.dluhc.notificationsapi.domain.SendNotificationRequest
import uk.gov.dluhc.notificationsapi.domain.SendNotificationResponse
import java.time.LocalDateTime
import java.util.UUID

@Component
class EmailNotificationFactory {

    fun createEmailNotification(
        notificationId: UUID,
        request: SendNotificationRequest,
        sendNotificationResponse: SendNotificationResponse,
        sentAt: LocalDateTime
    ) = EmailNotification(
        id = notificationId,
        type = request.notificationType,
        gssCode = request.gssCode,
        requestor = request.requestor,
        sourceType = request.sourceType,
        sourceReference = request.sourceReference,
        toEmail = request.emailAddress,
        personalisation = request.personalisation,
        notifyDetails = sendNotificationResponse,
        sentAt = sentAt
    )
}
