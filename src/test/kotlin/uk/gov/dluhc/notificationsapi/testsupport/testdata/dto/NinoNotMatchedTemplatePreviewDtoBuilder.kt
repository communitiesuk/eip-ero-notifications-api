package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildNinoNotMatchedTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: NinoNotMatchedPersonalisationDto = buildNinoNotMatchedPersonalisationDto()
): NinoNotMatchedTemplatePreviewDto =
    NinoNotMatchedTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )
