package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.GovNotifyNonRetryableException
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationMapper
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.SendNotificationRequestDto
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import java.util.UUID.randomUUID

private val logger = KotlinLogging.logger {}

@Service
class SendNotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationTemplateMapper: NotificationTemplateMapper,
    private val govNotifyApiClient: GovNotifyApiClient,
    private val notificationMapper: NotificationMapper,
    private val clock: Clock,
) {

    fun sendNotification(requestDto: SendNotificationRequestDto, personalisationMap: Map<String, String>) {
        val notificationId = randomUUID()
        val sentAt = LocalDateTime.now(clock)
        try {
            val sentNotification = sendNotificationForChannel(requestDto, personalisationMap, notificationId, sentAt)
            saveSentMessageOrLogError(sentNotification)
        } catch (ex: GovNotifyNonRetryableException) {
            logger.warn("Non-retryable error returned from the Notify service: ${ex.message}")
        }
    }

    private fun sendNotificationForChannel(
        request: SendNotificationRequestDto,
        personalisationMap: Map<String, String>,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        when (request.channel) {
            EMAIL -> {
                return sendGovNotifyEmail(request, personalisationMap, notificationId, sentAt)
            }

            else -> {
                TODO("Included in EIP1-3280")
            }
        }
    }

    private fun sendGovNotifyEmail(
        request: SendNotificationRequestDto,
        personalisationMap: Map<String, String>,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        with(request) {
            val templateId =
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(notificationType, EMAIL, language)
            val sendNotificationGovResponseDto =
                govNotifyApiClient.sendEmail(templateId, emailAddress, personalisationMap, notificationId)
            return notificationMapper.createNotification(
                notificationId = notificationId,
                request = request,
                personalisation = personalisationMap,
                sendNotificationResponse = sendNotificationGovResponseDto,
                sentAt = sentAt
            )
        }
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
