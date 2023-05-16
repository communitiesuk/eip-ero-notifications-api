package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedDocument
import uk.gov.dluhc.notificationsapi.models.RejectedDocumentPersonalisation

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class RejectedDocumentTemplatePreviewDtoMapper {

    @Autowired
    private lateinit var rejectedDocumentMapper: RejectedDocumentMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, generateRejectedDocumentTemplatePreviewRequest.getPersonalisation() ) )"
    )
    abstract fun toRejectedDocumentTemplatePreviewDto(generateRejectedDocumentTemplatePreviewRequest: GenerateRejectedDocumentTemplatePreviewRequest): RejectedDocumentTemplatePreviewDto

    @Mapping(
        target = "documents",
        expression = "java( mapDocuments( languageDto, personalisation.getDocuments() ) )"
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: RejectedDocumentPersonalisation
    ): RejectedDocumentPersonalisationDto

    fun mapDocuments(languageDto: LanguageDto, documents: List<RejectedDocument>) =
        documents.map { document -> rejectedDocumentMapper.fromApiRejectedDocumentToString(languageDto, document) }
}
