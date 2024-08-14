package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.GenerateInviteToRegisterTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.InviteToRegisterPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, CommunicationChannelMapper::class, SourceTypeMapper::class])
abstract class InviteToRegisterTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var sourceTypeMapper: SourceTypeMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )",
    )
    @Mapping(
        target = "notificationType",
        constant = "INVITE_TO_REGISTER",
    )
    abstract fun toDto(request: GenerateInviteToRegisterTemplatePreviewRequest): InviteToRegisterTemplatePreviewDto

    @Mapping(
        target = "personalisationFullSourceTypeString",
        expression = "java( sourceTypeMapper.toFullSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        source = "personalisation.freeText",
        target = "freeText",
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: InviteToRegisterPersonalisation,
        sourceType: SourceType,
    ): InviteToRegisterPersonalisationDto
}
