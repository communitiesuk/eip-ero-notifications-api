package uk.gov.dluhc.notificationsapi.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.exception.SdkServiceException
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.database.repository.EmailNotificationRepository
import uk.gov.dluhc.notificationsapi.domain.EmailNotification
import uk.gov.dluhc.notificationsapi.domain.SendNotificationRequest
import uk.gov.dluhc.notificationsapi.factory.EmailNotificationFactory
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID.randomUUID

private val logger = KotlinLogging.logger {}

@Service
class SendEmailNotificationService(
    private val emailNotificationRepository: EmailNotificationRepository,
    private val govNotifyApiClient: GovNotifyApiClient,
    private val emailNotificationFactory: EmailNotificationFactory,
    private val clock: Clock,
) {

    fun sendEmailNotification(request: SendNotificationRequest) {
        val notificationId = randomUUID()
        val sentAt = LocalDateTime.now(clock)
        val sendNotificationDto =
            govNotifyApiClient.sendEmail(
                request.notificationType,
                request.emailAddress,
                request.personalisation,
                notificationId
            )
        val emailNotification =
            emailNotificationFactory.createEmailNotification(notificationId, request, sendNotificationDto, sentAt)
        saveSentMessageOrLogError(emailNotification)
    }

    private fun saveSentMessageOrLogError(emailNotification: EmailNotification) {
        try {
            emailNotificationRepository.saveEmailNotification(emailNotification)
        } catch (error: SdkClientException) {
            logger.error { "Client error attempting to save 'sent email notification': $error" }
        } catch (error: SdkServiceException) {
            logger.error { "Service error attempting to save 'sent email notification': $error" }
        }
    }
}
