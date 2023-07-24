package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.GovNotifyNonRetryableException
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.database.entity.Notification
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationAuditMapper
import uk.gov.dluhc.notificationsapi.database.mapper.NotificationMapper
import uk.gov.dluhc.notificationsapi.database.repository.NotificationAuditRepository
import uk.gov.dluhc.notificationsapi.database.repository.NotificationRepository
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.LETTER
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
    private val notificationAuditRepository: NotificationAuditRepository,
    private val notificationAuditMapper: NotificationAuditMapper,
    private val clock: Clock,
) {

    fun sendNotification(requestDto: SendNotificationRequestDto, personalisationMap: Map<String, Any>) {
        val notificationId = randomUUID()
        val sentAt = LocalDateTime.now(clock)
        try {
            val sentNotification = sendNotificationForChannel(requestDto, personalisationMap, notificationId, sentAt)
            saveSentMessageAndCreateAuditOrLogError(sentNotification)
        } catch (ex: GovNotifyNonRetryableException) {
            logger.warn("Non-retryable error returned from the Notify service: ${ex.message}")
        }
    }

    private fun sendNotificationForChannel(
        request: SendNotificationRequestDto,
        personalisationMap: Map<String, Any>,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        return when (request.channel) {
            EMAIL -> {
                sendGovNotifyEmail(request, personalisationMap, notificationId, sentAt)
            }

            LETTER -> {
                sendGovNotifyLetter(request, personalisationMap, notificationId, sentAt)
            }
        }
    }

    private fun sendGovNotifyEmail(
        request: SendNotificationRequestDto,
        personalisationMap: Map<String, Any>,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        with(request) {
            val templateId =
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, EMAIL, language)
            val sendNotificationGovResponseDto =
                govNotifyApiClient.sendEmail(templateId, toAddress.emailAddress!!, personalisationMap, notificationId)
            return notificationMapper.createNotification(
                notificationId = notificationId,
                request = request,
                personalisation = personalisationMap,
                sendNotificationResponse = sendNotificationGovResponseDto,
                sentAt = sentAt
            )
        }
    }

    private fun sendGovNotifyLetter(
        request: SendNotificationRequestDto,
        personalisationMap: Map<String, Any>,
        notificationId: UUID,
        sentAt: LocalDateTime
    ): Notification {
        with(request) {
            val templateId =
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(sourceType, notificationType, LETTER, language)
            val sendNotificationGovResponseDto =
                govNotifyApiClient.sendLetter(templateId, toAddress.postalAddress!!, personalisationMap, notificationId)
            return notificationMapper.createNotification(
                notificationId = notificationId,
                request = request,
                personalisation = personalisationMap,
                sendNotificationResponse = sendNotificationGovResponseDto,
                sentAt = sentAt
            )
        }
    }

    private fun saveSentMessageAndCreateAuditOrLogError(notification: Notification) {
        try {
            notificationRepository.saveNotification(notification)
            notificationAuditRepository.saveNotificationAudit(notificationAuditMapper.createNotificationAudit(notification))
        } catch (error: SdkClientException) {
            logger.error { "Client error attempting to save Notification: $error" }
        } catch (error: SdkServiceException) {
            logger.error { "Service error attempting to save Notification: $error" }
        }
    }
}
