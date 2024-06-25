package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationReceivedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGenerateApplicationReceivedTemplatePreviewDto(
    sourceType: SourceType,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: ApplicationReceivedPersonalisationDto = buildApplicationReceivedPersonalisationDto(),
): ApplicationReceivedTemplatePreviewDto =
    ApplicationReceivedTemplatePreviewDto(
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
    )
