package uk.gov.dluhc.notificationsapi.client

import mu.KotlinLogging
import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto
import uk.gov.service.notify.NotificationClient

private val logger = KotlinLogging.logger {}

/**
 * Client class for interacting with UK Government `Notify` REST API
 */
@Component
class GovNotifyApiClient(private val notificationClient: NotificationClient) {

    fun sendEmail() {
        val sendEmailResponse = notificationClient.sendEmail("templateId", "emailAddress", null, "reference")
        logger.info { "Email response: $sendEmailResponse" }
    }

    fun generateTemplatePreview(templateId: String, personalisation: Map<String, Any>): NotifyTemplatePreviewDto =
        notificationClient.generateTemplatePreview(templateId, personalisation).run {
            NotifyTemplatePreviewDto(body, html.orElse(null))
        }
}
