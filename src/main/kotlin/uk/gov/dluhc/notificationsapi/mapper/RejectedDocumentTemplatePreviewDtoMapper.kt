package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class RejectedDocumentTemplatePreviewDtoMapper {

    @Autowired
    lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, request.getPersonalisation(), request.getSourceType() ) )",
    )
    abstract fun toRejectedDocumentTemplatePreviewDto(request: GenerateRejectedDocumentTemplatePreviewRequest): RejectedDocumentTemplatePreviewDto

    @Mapping(
        target = "personalisationSourceTypeString",
        expression = "java( sourceTypeMapper.toSourceTypeString( sourceType, languageDto ) )",
    )
    @Mapping(
        target = "documents",
        expression = "java( rejectedDocumentsMapper.mapRejectionDocumentsFromApi( languageDto, personalisation.getDocuments() ) )",
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: RejectedDocumentPersonalisation,
        sourceType: SourceType,
    ): RejectedDocumentPersonalisationDto
}
