package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildApplicationRejectedPersonalisationDto

fun buildApplicationRejectedTemplatePreviewDto(
    sourceType: SourceType,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: ApplicationRejectedPersonalisationDto = buildApplicationRejectedPersonalisationDto()
): ApplicationRejectedTemplatePreviewDto =
    ApplicationRejectedTemplatePreviewDto(
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )
