package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.factory.NotificationFactory
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import java.util.UUID.randomUUID

private val logger = KotlinLogging.logger {}

@Service
class SendNotificationService(
    private val notificationRepository: NotificationRepository,
    private val govNotifyApiClient: GovNotifyApiClient,
    private val notificationFactory: NotificationFactory,
    private val clock: Clock,
) {

    fun sendNotification(request: SendNotificationRequestDto) {
        val notificationId = randomUUID()
        val sentAt = LocalDateTime.now(clock)
        val notification = sendNotificationForChannel(request, notificationId, sentAt)
        saveSentMessageOrLogError(notification)
    }

    private fun sendNotificationForChannel(
        request: SendNotificationRequestDto,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        return when (request.channel) {
            NotificationChannel.EMAIL -> sendEmailNotification(request, notificationId, sentAt)
        }
    }

    private fun sendEmailNotification(
        request: SendNotificationRequestDto,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        val sendNotificationDto =
            govNotifyApiClient.sendEmail(
                request.notificationType,
                request.emailAddress,
                request.personalisation,
                notificationId
            )
        return notificationFactory.createNotification(notificationId, request, sendNotificationDto, sentAt)
    }

    private fun saveSentMessageOrLogError(notification: Notification) {
        try {
            notificationRepository.saveNotification(notification)
        } catch (error: SdkClientException) {
            logger.error { "Client error attempting to save Notification: $error" }
        } catch (error: SdkServiceException) {
            logger.error { "Service error attempting to save Notification: $error" }
        }
    }
}
