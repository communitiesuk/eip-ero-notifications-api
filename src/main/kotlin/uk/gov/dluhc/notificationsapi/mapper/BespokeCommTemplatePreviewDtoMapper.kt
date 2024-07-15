package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.BespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateBespokeCommTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.SourceType
import java.time.LocalDate

@Mapper(uses = [LanguageMapper::class, CommunicationChannelMapper::class, SourceTypeMapper::class])
abstract class BespokeCommTemplatePreviewDtoMapper {

    @Autowired
    protected lateinit var deadlineMapper: DeadlineMapper

    @Autowired
    protected lateinit var sourceTypeMapper: SourceTypeMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )",
    )
    @Mapping(
        target = "notificationType",
        constant = "BESPOKE_COMM",
    )
    abstract fun toDto(request: GenerateBespokeCommTemplatePreviewRequest): BespokeCommTemplatePreviewDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( sourceTypeMapper.toSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        source = "personalisation.subjectHeader",
        target = "subjectHeader",
    )
    @Mapping(
        source = "personalisation.details",
        target = "details",
    )
    @Mapping(
        source = "personalisation.whatToDo",
        target = "whatToDo",
    )
    @Mapping(
        target = "deadline",
        expression = "java( mapDeadline( personalisation.getDeadlineDate(), personalisation.getDeadlineTime(), languageDto, sourceTypeMapper.toFullSourceTypeString( sourceType, languageDto ) ) )",
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: BespokeCommPersonalisation,
        sourceType: SourceType,
    ): BespokeCommPersonalisationDto

    protected fun mapDeadline(
        deadlineDate: LocalDate?,
        deadlineTime: String?,
        languageDto: LanguageDto,
        sourceTypeString: String,
    ): String? = deadlineDate?.let {
        deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, languageDto, sourceTypeString)
    }
}
