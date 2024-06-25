package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.RequiredDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildNinoNotMatchedTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    notificationType: NotificationType = NotificationType.NINO_NOT_MATCHED,
    personalisation: RequiredDocumentPersonalisationDto = buildNinoNotMatchedPersonalisationDto(),
): NinoNotMatchedTemplatePreviewDto =
    NinoNotMatchedTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
        notificationType = notificationType,
    )
