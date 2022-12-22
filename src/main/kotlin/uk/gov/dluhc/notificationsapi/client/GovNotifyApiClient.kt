package uk.gov.dluhc.notificationsapi.client

import mu.KotlinLogging
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.client.mapper.SendNotificationResponseMapper
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.service.notify.NotificationClient
import uk.gov.service.notify.NotificationClientException
import java.util.UUID

private val logger = KotlinLogging.logger {}

/**
 * Client class for interacting with UK Government `Notify` REST API
 */
@Component
class GovNotifyApiClient(
    private val notificationClient: NotificationClient,
    private val sendNotificationResponseMapper: SendNotificationResponseMapper
) {

    fun sendEmail(
        templateId: String,
        emailAddress: String,
        personalisation: Map<String, String>,
        notificationId: UUID
    ): SendNotificationResponseDto {
        try {
            logger.info { "Sending email for templateId [$templateId], notificationId [$notificationId]" }
            return notificationClient.sendEmail(templateId, emailAddress, personalisation, notificationId.toString())
                .run {
                    sendNotificationResponseMapper.toSendNotificationResponse(this)
                }
        } catch (ex: NotificationClientException) {
            throw logAndThrowGovNotifyApiException("Send email", ex, templateId)
        }
    }

    fun generateTemplatePreview(templateId: String, personalisation: Map<String, String>): NotifyTemplatePreviewDto =
        try {
            logger.info { "Generating template preview for templateId [$templateId]" }
            notificationClient.generateTemplatePreview(templateId, personalisation).run {
                NotifyTemplatePreviewDto(body, subject.orElse(null), html.orElse(null))
            }
        } catch (ex: NotificationClientException) {
            throw logAndThrowGovNotifyApiException("Generating template preview", ex, templateId)
        }

    private fun logAndThrowGovNotifyApiException(
        callDescription: String,
        ex: NotificationClientException,
        templateId: String
    ): GovNotifyApiException {
        val message = ex.message ?: ""
        when (ex.httpResult) {
            400 -> {
                logger.warn { "$callDescription failed. [${ex.message}]" }
                throw GovNotifyApiBadRequestException(message)
            }

            404 -> {
                logger.warn { "$callDescription failed. Template [$templateId] not found." }
                throw GovNotifyApiNotFoundException(message)
            }

            else -> throw GovNotifyApiGeneralException(message)
        }
    }
}
