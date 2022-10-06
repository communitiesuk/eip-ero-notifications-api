package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.dto.api.NotifyTemplatePreviewDto

@Service
class TemplateService(private val govNotifyApiClient: GovNotifyApiClient) {
    fun generateTemplatePreview(request: GenerateTemplatePreviewRequestDto): NotifyTemplatePreviewDto =
        with(request) { govNotifyApiClient.generateTemplatePreview(templateId, personalisation) }
}
