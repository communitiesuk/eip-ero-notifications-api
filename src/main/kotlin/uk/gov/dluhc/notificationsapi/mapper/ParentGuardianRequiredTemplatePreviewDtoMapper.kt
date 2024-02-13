package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.GenerateParentGuardianRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateParentGuardianRequiredTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface ParentGuardianRequiredTemplatePreviewDtoMapper {

    fun toParentGuardianRequiredTemplatePreviewDto(request: GenerateParentGuardianRequiredTemplatePreviewRequest): GenerateParentGuardianRequiredTemplatePreviewDto

}