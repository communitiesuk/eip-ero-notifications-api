package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildRejectedDocumentTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: RejectedDocumentPersonalisationDto = buildRejectedDocumentPersonalisationDto()
): RejectedDocumentTemplatePreviewDto =
    RejectedDocumentTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )
