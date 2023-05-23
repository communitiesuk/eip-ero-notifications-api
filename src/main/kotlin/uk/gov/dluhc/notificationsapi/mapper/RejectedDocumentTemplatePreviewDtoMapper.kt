package uk.gov.dluhc.notificationsapi.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedDocumentPersonalisation

@Mapper(uses = [LanguageMapper::class, SourceTypeMapper::class])
abstract class RejectedDocumentTemplatePreviewDtoMapper {

    @Autowired
    lateinit var rejectedDocumentReasonMapper: RejectedDocumentReasonMapper

    @Autowired
    lateinit var rejectedDocumentTypeMapper: RejectedDocumentTypeMapper

    @Mapping(
        target = "personalisation",
        expression = "java( mapPersonalisation( language, generateRejectedDocumentTemplatePreviewRequest.getPersonalisation() ) )"
    )
    abstract fun toRejectedDocumentTemplatePreviewDto(generateRejectedDocumentTemplatePreviewRequest: GenerateRejectedDocumentTemplatePreviewRequest): RejectedDocumentTemplatePreviewDto

    @Mapping(
        target = "documents",
        expression = "java( mapDocuments( languageDto, personalisation ) )"
    )
    abstract fun mapPersonalisation(
        languageDto: LanguageDto,
        personalisation: RejectedDocumentPersonalisation
    ): RejectedDocumentPersonalisationDto

    fun mapDocuments(
        languageDto: LanguageDto,
        personalisation: RejectedDocumentPersonalisation
    ): List<String> {
        return personalisation.documents.map { document ->
            val docType = rejectedDocumentTypeMapper.toDocumentTypeString(document.documentType, languageDto)
            // EIP1-4790 introduced multiple rejection reasons - currently only the first if present is mapped to the template
            val docReason = document.rejectionReasons
                .firstOrNull()
                ?.let { rejectedDocumentReasonMapper.toDocumentRejectionReasonString(it, languageDto) }
            docType.appendIfNotNull(docReason).appendIfNotNull(document.rejectionNotes)
        }
    }

    private fun String.appendIfNotNull(value: String?) = this + (" - $value".takeIf { value != null } ?: "")
}
