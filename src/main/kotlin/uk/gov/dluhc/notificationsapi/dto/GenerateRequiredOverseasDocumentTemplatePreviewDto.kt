package uk.gov.dluhc.notificationsapi.dto

class GenerateRequiredOverseasDocumentTemplatePreviewDto(
    val channel: CommunicationChannel,
    val language: LanguageDto,
    val personalisation: RequiredOverseasDocumentPersonalisationDto,
    val documentCategory: DocumentCategoryDto,
)
