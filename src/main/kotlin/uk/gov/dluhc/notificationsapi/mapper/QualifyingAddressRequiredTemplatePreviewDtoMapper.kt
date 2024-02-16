package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import uk.gov.dluhc.notificationsapi.dto.api.GenerateQualifyingAddressRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateQualifyingAddressRequiredTemplatePreviewRequest

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
interface QualifyingAddressRequiredTemplatePreviewDtoMapper {

    fun toQualifyingAddressRequiredTemplatePreviewDto(request: GenerateQualifyingAddressRequiredTemplatePreviewRequest): GenerateQualifyingAddressRequiredTemplatePreviewDto
}
