package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVotePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVoteTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.GenerateNotRegisteredToVoteTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.NotRegisteredToVotePersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, CommunicationChannelMapper::class, SourceTypeMapper::class])
abstract class NotRegisteredToVoteTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var sourceTypeMapper: SourceTypeMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )",
    )
    @Mapping(
        target = "notificationType",
        constant = "NOT_REGISTERED_TO_VOTE",
    )
    abstract fun toDto(request: GenerateNotRegisteredToVoteTemplatePreviewRequest): NotRegisteredToVoteTemplatePreviewDto

    @Mapping(
        target = "personalisationFullSourceTypeString",
        expression = "java( sourceTypeMapper.toFullSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        source = "personalisation.freeText",
        target = "freeText",
    )
    @Mapping(
        source = "personalisation.property",
        target = "property",
    )
    @Mapping(
        source = "personalisation.street",
        target = "street",
    )
    @Mapping(
        source = "personalisation.town",
        target = "town",
    )
    @Mapping(
        source = "personalisation.area",
        target = "area",
    )
    @Mapping(
        source = "personalisation.locality",
        target = "locality",
    )
    @Mapping(
        source = "personalisation.postcode",
        target = "postcode",
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: NotRegisteredToVotePersonalisation,
        sourceType: SourceType,
    ): NotRegisteredToVotePersonalisationDto
}
