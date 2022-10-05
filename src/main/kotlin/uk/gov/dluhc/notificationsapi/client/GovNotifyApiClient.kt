package uk.gov.dluhc.notificationsapi.client

import mu.KotlinLogging
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.database.entity.NotificationType
import uk.gov.service.notify.NotificationClient
import java.util.UUID

private val logger = KotlinLogging.logger {}

/**
 * Client class for interacting with UK Government `Notify` REST API
 */
@Component
class GovNotifyApiClient(
    private val notificationClient: NotificationClient,
    private val notificationTemplateMapper: NotificationTemplateMapper
) {

    fun sendEmail(
        notificationType: NotificationType,
        emailAddress: String,
        personalisation: Map<String, Any>,
        notificationId: UUID
    ) {
        val templateId = notificationTemplateMapper.fromNotificationType(notificationType)
        val sendEmailResponse =
            notificationClient.sendEmail(templateId, emailAddress, personalisation, notificationId.toString())
        logger.info { "Email response: $sendEmailResponse" }
    }

    fun generateTemplatePreview(templateId: String, personalisation: Map<String, Any>): NotifyTemplatePreviewDto =
        notificationClient.generateTemplatePreview(templateId, personalisation).run {
            NotifyTemplatePreviewDto(body, html.orElse(null))
        }
}
