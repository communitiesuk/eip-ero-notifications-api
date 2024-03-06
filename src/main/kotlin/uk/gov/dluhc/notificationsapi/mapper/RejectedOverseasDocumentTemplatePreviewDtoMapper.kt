package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedOverseasDocumentPersonalisation

@Component
class RejectedOverseasDocumentTemplatePreviewDtoMapper(
    private val languageMapper: LanguageMapper,
    private val notificationChannelMapper: NotificationChannelMapper,
    private val rejectedDocumentsMapper: RejectedDocumentsMapper,
    private val eroDtoMapper: EroDtoMapper,
    private val documentCategoryMapper: DocumentCategoryMapper
) {

    fun toRejectedOverseasDocumentTemplatePreviewDto(request: GenerateRejectedOverseasDocumentTemplatePreviewRequest): GenerateRejectedOverseasDocumentTemplatePreviewDto {
        with(request) {
            return GenerateRejectedOverseasDocumentTemplatePreviewDto(
                channel = notificationChannelMapper.fromApiToDto(channel),
                language = languageMapper.fromApiToDto(language!!),
                documentCategory = documentCategoryMapper.fromApiToDto(documentCategory),
                personalisation = mapPersonalisation(personalisation, languageMapper.fromApiToDto(language))
            )
        }
    }

    private fun mapPersonalisation(
        personalisation: RejectedOverseasDocumentPersonalisation,
        language: LanguageDto
    ): RejectedOverseasDocumentPersonalisationDto {
        with(personalisation) {
            return RejectedOverseasDocumentPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroDtoMapper.toContactDetailsDto(eroContactDetails),
                rejectedDocumentFreeText = rejectedDocumentFreeText,
                documents = rejectedDocumentsMapper.mapRejectionDocumentsFromApi(language, documents)

            )
        }
    }
}
