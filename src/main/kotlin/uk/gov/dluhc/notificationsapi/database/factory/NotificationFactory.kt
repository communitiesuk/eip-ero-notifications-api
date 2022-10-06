package uk.gov.dluhc.notificationsapi.database.factory

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.database.entity.Channel.EMAIL
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType.APPLICATION_APPROVED
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType.APPLICATION_RECEIVED
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType.APPLICATION_REJECTED
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType.PHOTO_RESUBMISSION
import uk.gov.dluhc.notificationsapi.database.entity.NotifyDetails
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import java.time.LocalDateTime
import java.util.UUID
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as EntitySourceType
import uk.gov.dluhc.notificationsapi.dto.NotificationType as DtoNotificationType

@Component
class NotificationFactory {

    fun createNotification(
        notificationId: UUID,
        request: SendNotificationRequestDto,
        sendNotificationResponse: SendNotificationResponseDto,
        sentAt: LocalDateTime
    ) = Notification(
        id = notificationId,
        type = toNotificationType(request.notificationType),
        channel = EMAIL,
        gssCode = request.gssCode,
        requestor = request.requestor,
        sourceType = toSourceType(request.sourceType),
        sourceReference = request.sourceReference,
        toEmail = request.emailAddress,
        personalisation = request.personalisation,
        notifyDetails = toNotifyDetails(sendNotificationResponse),
        sentAt = sentAt
    )

    fun toSourceType(sourceType: SourceType): EntitySourceType {
        return when (sourceType) {
            SourceType.VOTER_CARD -> EntitySourceType.VOTER_CARD
        }
    }

    fun toNotificationType(notificationType: DtoNotificationType): NotificationType {
        return when (notificationType) {
            DtoNotificationType.APPLICATION_RECEIVED -> APPLICATION_RECEIVED
            DtoNotificationType.APPLICATION_APPROVED -> APPLICATION_APPROVED
            DtoNotificationType.APPLICATION_REJECTED -> APPLICATION_REJECTED
            DtoNotificationType.PHOTO_RESUBMISSION -> PHOTO_RESUBMISSION
        }
    }

    fun toNotifyDetails(sendNotificationResponse: SendNotificationResponseDto): NotifyDetails =
        NotifyDetails(
            notificationId = sendNotificationResponse.notificationId,
            reference = sendNotificationResponse.reference,
            templateId = sendNotificationResponse.templateId,
            templateVersion = sendNotificationResponse.templateVersion,
            templateUri = sendNotificationResponse.templateUri,
            body = sendNotificationResponse.body,
            subject = sendNotificationResponse.subject,
            fromEmail = sendNotificationResponse.fromEmail
        )
}
