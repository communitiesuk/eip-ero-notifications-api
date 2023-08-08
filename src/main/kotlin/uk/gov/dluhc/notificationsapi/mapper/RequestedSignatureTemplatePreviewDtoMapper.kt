package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRequestedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RequestedSignaturePersonalisation

@Mapper(uses = [LanguageMapper::class, NotificationChannelMapper::class, SourceTypeMapper::class])
abstract class RequestedSignatureTemplatePreviewDtoMapper {

    @Mapping(
        target = "notificationType",
        constant = "REQUESTED_SIGNATURE"
    )
    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation() ) )"
    )
    abstract fun toRequestedSignatureTemplatePreviewDto(
        request: GenerateRequestedSignatureTemplatePreviewRequest
    ): RequestedSignatureTemplatePreviewDto

    protected abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: RequestedSignaturePersonalisation
    ): RequestedSignaturePersonalisationDto
}
