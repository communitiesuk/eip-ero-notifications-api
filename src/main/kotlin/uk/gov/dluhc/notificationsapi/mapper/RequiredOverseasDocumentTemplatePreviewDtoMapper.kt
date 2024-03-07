package uk.gov.dluhc.notificationsapi.mapper

import org.springframework.stereotype.Component
import uk.gov.dluhc.notificationsapi.dto.GenerateRequiredOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.RequiredOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateRequiredOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RequiredOverseasDocumentPersonalisation

@Component
class RequiredOverseasDocumentTemplatePreviewDtoMapper(
    private val languageMapper: LanguageMapper,
    private val notificationChannelMapper: NotificationChannelMapper,
    private val eroDtoMapper: EroDtoMapper,
    private val documentCategoryMapper: DocumentCategoryMapper
) {

    fun toRequiredOverseasDocumentTemplatePreviewDto(request: GenerateRequiredOverseasDocumentTemplatePreviewRequest): GenerateRequiredOverseasDocumentTemplatePreviewDto {
        with(request) {
            return GenerateRequiredOverseasDocumentTemplatePreviewDto(
                channel = notificationChannelMapper.fromApiToDto(channel),
                language = languageMapper.fromApiToDto(language!!),
                documentCategory = documentCategoryMapper.fromApiToDto(documentCategory),
                personalisation = mapPersonalisation(personalisation)
            )
        }
    }

    private fun mapPersonalisation(
        personalisation: RequiredOverseasDocumentPersonalisation,
    ): RequiredOverseasDocumentPersonalisationDto {
        with(personalisation) {
            return RequiredOverseasDocumentPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroDtoMapper.toContactDetailsDto(eroContactDetails),
                requiredDocumentFreeText = requiredDocumentFreeText,

            )
        }
    }
}
