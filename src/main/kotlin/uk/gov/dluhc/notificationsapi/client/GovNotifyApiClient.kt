package uk.gov.dluhc.notificationsapi.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.client.mapper.SendNotificationResponseMapper
import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.SendNotificationResponseDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
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
    private val sendNotificationResponseMapper: SendNotificationResponseMapper,
    @Value("\${api.notify.ignore-wrong-api-key-errors}") private val ignoreWrongApiKeyErrors: Boolean,
) {

    fun sendEmail(
        templateId: String,
        emailAddress: String,
        personalisation: Map<String, Any>,
        notificationId: UUID,
    ): SendNotificationResponseDto? {
        try {
            logger.info { "Sending email for templateId [$templateId], notificationId [$notificationId]" }
            return notificationClient.sendEmail(templateId, emailAddress, personalisation, notificationId.toString())
                .run {
                    sendNotificationResponseMapper.toSendNotificationResponse(this)
                }
        } catch (ex: NotificationClientException) {
            if (ignoreWrongApiKeyErrors && ex.isWrongApiKeyError()) {
                logger.info(
                    "This environment's API key does not support sending emails to the specified email address " +
                        "as it is not on the team members list. GOV Notify returned an error, but this notification " +
                        "will be treated as being successfully sent.",
                )
                return null
            }
            throw logAndThrowGovNotifyApiException("Send email", ex, templateId)
        }
    }

    fun sendLetter(
        templateId: String,
        toAddress: NotificationDestinationDto,
        placeholders: Map<String, Any>,
        notificationId: UUID,
        sourceType: SourceType,
    ): SendNotificationResponseDto? {
        try {
            logger.info { "Sending letter for templateId [$templateId], notificationId [$notificationId]" }
            val personalisation = placeholders + getLetterAddress(toAddress, sourceType)
            return notificationClient.sendLetter(templateId, personalisation, notificationId.toString())
                .run {
                    sendNotificationResponseMapper.toSendNotificationResponse(this)
                }
        } catch (ex: NotificationClientException) {
            if (ignoreWrongApiKeyErrors && ex.isWrongApiKeyError()) {
                logger.info(
                    "This environment's API key does not support sending letters. GOV Notify returned an error, " +
                        "but this notification will be treated as being successfully sent.",
                )
                return null
            }
            throw logAndThrowGovNotifyApiException("Send letter", ex, templateId)
        }
    }

    fun generateTemplatePreview(templateId: String, personalisation: Map<String, Any>): NotifyTemplatePreviewDto =
        try {
            logger.info { "Generating template preview for templateId [$templateId]" }
            notificationClient.generateTemplatePreview(templateId, personalisation).run {
                NotifyTemplatePreviewDto(body, subject.orElse(null), html.orElse(null))
            }
        } catch (ex: NotificationClientException) {
            throw logAndThrowGovNotifyApiException("Generating template preview", ex, templateId)
        }

    private fun getLetterAddress(
        toAddress: NotificationDestinationDto,
        sourceType: SourceType,
    ): Map<String, String?> {
        return if (sourceType == SourceType.OVERSEAS) {
            toAddress.overseasElectorAddress?.toPersonalisationMap() ?: toAddress.postalAddress?.toPersonalisationMap()
                ?: throw IllegalArgumentException("One of OverseasElectorAddress or PostalAddress is required with given sourceType: $sourceType")
        } else {
            toAddress.postalAddress?.toPersonalisationMap()
                ?: throw IllegalArgumentException("PostalAddress is required with given sourceType: $sourceType")
        }
    }

    private fun logAndThrowGovNotifyApiException(
        callDescription: String,
        ex: NotificationClientException,
        templateId: String,
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

    fun NotificationClientException.isWrongApiKeyError() =
        this.message != null &&
            (this.httpResult == 400 && this.message!!.contains("send to this recipient using a team-only API key")) ||
            (this.httpResult == 403 && this.message!!.contains("Cannot send letters with a team api key"))
}
