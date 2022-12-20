package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.client.mapper.NotificationTemplateMapper
import uk.gov.dluhc.notificationsapi.dto.GenerateTemplatePreviewRequestDto
import uk.gov.dluhc.notificationsapi.models.GenerateTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.TemplateType

@Mapper
@Deprecated(message = "Use template specific method")
abstract class GenerateTemplatePreviewRequestDtoMapper {
    @Autowired
    private lateinit var notificationTemplateMapper: NotificationTemplateMapper

    @Autowired
    private lateinit var notificationTypeMapper: NotificationTypeMapper

    @Mapping(target = "templateId", expression = "java( getTemplateId(templateType) )")
    @Mapping(target = "personalisation", source = "request.personalisation")
    @Deprecated(message = "Use template specific method")
    abstract fun toGenerateTemplatePreviewRequestDto(
        templateType: TemplateType,
        request: GenerateTemplatePreviewRequest
    ): GenerateTemplatePreviewRequestDto

    protected fun getTemplateId(templateType: TemplateType): String =
        notificationTemplateMapper.fromNotificationType(notificationTypeMapper.toNotificationType(templateType))
}
