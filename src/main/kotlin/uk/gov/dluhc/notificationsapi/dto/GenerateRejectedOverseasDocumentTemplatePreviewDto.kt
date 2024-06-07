package uk.gov.dluhc.notificationsapi.dto

class GenerateRejectedOverseasDocumentTemplatePreviewDto(
    val channel: CommunicationChannel,
    val language: LanguageDto,
    val personalisation: RejectedOverseasDocumentPersonalisationDto,
    val documentCategory: DocumentCategoryDto,
)
