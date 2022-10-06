package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.client.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.TemplateType

@Mapper
abstract class GenerateTemplatePreviewRequestDtoMapper {
    @Autowired
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Autowired
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mapping(expression = "java( getTemplateId(templateType) )", target = "templateId")
    @Mapping(source = "request.personalisation", target = "personalisation")
    abstract fun toGenerateTemplatePreviewRequestDto(
        templateType: TemplateType,
        request: GenerateTemplatePreviewRequest
    ): GenerateTemplatePreviewRequestDto

    fun getTemplateId(templateType: TemplateType): String =
        notificationTemplateMapper.fromNotificationType(notificationTypeMapper.toNotificationType(templateType))
}
