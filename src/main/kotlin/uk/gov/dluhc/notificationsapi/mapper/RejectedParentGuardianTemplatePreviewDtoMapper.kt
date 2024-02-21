package uk.gov.dluhc.notificationsapi.mapper

import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedParentGuardianTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedParentGuardianPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedParentGuardianTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.RejectedParentGuardianPersonalisation

class RejectedParentGuardianTemplatePreviewDtoMapper(
    private val languageMapper: LanguageMapper,
    private val notificationChannelMapper: NotificationChannelMapper,
    private val sourceTypeMapper: SourceTypeMapper,
    private val rejectedDocumentsMapper: RejectedDocumentsMapper,
    private val eroDtoMapper: EroDtoMapper
) {

    fun toRejectedParentGuardianTemplatePreviewDto(request: GenerateRejectedParentGuardianTemplatePreviewRequest): GenerateRejectedParentGuardianTemplatePreviewDto {
        with(request) {
            return GenerateRejectedParentGuardianTemplatePreviewDto(
                sourceType = sourceTypeMapper.fromApiToDto(sourceType),
                channel = notificationChannelMapper.fromApiToDto(channel),
                language = languageMapper.fromApiToDto(language!!),
                personalisation = mapPersonalisation(personalisation, languageMapper.fromApiToDto(language))
            )
        }
    }

    private fun mapPersonalisation(
        personalisation: RejectedParentGuardianPersonalisation,
        language: LanguageDto
    ): RejectedParentGuardianPersonalisationDto {
        with(personalisation) {
            return RejectedParentGuardianPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroDtoMapper.toContactDetailsDto(eroContactDetails),
                rejectedDocumentFreeText = rejectedDocumentFreeText,
                documents = rejectedDocumentsMapper.mapRejectionDocumentsFromApi(language, documents)

            )
        }
    }
}
