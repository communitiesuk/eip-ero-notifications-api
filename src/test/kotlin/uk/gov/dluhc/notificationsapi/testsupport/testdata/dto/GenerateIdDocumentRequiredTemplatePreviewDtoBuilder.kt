package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGenerateIdDocumentRequiredTemplatePreviewDto(
    sourceType: SourceType = SourceType.VOTER_CARD,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: IdDocumentRequiredPersonalisationDto = buildIdDocumentRequiredPersonalisationDto(),
) = GenerateIdDocumentRequiredTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    personalisation = personalisation,
)
