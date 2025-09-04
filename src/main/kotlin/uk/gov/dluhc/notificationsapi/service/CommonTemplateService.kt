package uk.gov.dluhc.notificationsapi.service

import org.springframework.stereotype.Service
import uk.gov.dluhc.notificationsapi.client.GovNotifyApiClient
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.BaseGenerateTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommonTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.mapper.CommonTemplatePreviewDtoMapper
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewResponse
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType

@Service
class CommonTemplateService(
    private val govNotifyApiClient: GovNotifyApiClient,
    private val notificationTemplateMapper: NotificationTemplateMapper,
    private val commonTemplatePreviewDtoMapper: CommonTemplatePreviewDtoMapper,
) {
    fun generateTemplatePreview(
        channel: CommunicationChannel,
        sourceType: SourceType,
        language: Language?,
        notificationTypeDto: NotificationType,
        getTemplatePersonalisation: (CommonTemplatePreviewDto) -> Map<String, Any>
    ): GenerateTemplatePreviewResponse {
        val commonTemplatePreviewDto = commonTemplatePreviewDtoMapper.toCommonTemplatePreviewDto(
            channel,
            sourceType,
            language,
            notificationTypeDto,
        )

        val personalisation = getTemplatePersonalisation(commonTemplatePreviewDto)

         val notifyTemplatePreviewDto =
            govNotifyApiClient.generateTemplatePreview(
                notificationTemplateMapper.fromNotificationTypeForChannelInLanguage(
                    commonTemplatePreviewDto
                ),
                personalisation
            )

        return with(notifyTemplatePreviewDto){
            GenerateTemplatePreviewResponse(text, subject, html)
        }
    }
}
