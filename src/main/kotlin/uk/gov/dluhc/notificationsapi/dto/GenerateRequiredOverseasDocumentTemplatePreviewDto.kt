package uk.gov.dluhc.notificationsapi.dto

class GenerateRequiredOverseasDocumentTemplatePreviewDto(
    val channel: NotificationChannel,
    val language: LanguageDto,
    val personalisation: RequiredOverseasDocumentPersonalisationDto,
    val documentCategory: DocumentCategoryDto
)
