package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.GenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGenerateApplicationApprovedTemplatePreviewDto(
    sourceType: SourceType,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: ApplicationApprovedPersonalisationDto = buildApplicationApprovedPersonalisationDto()
): GenerateApplicationApprovedTemplatePreviewDto =
    GenerateApplicationApprovedTemplatePreviewDto(
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )
