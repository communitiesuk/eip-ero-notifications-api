package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGeneratePhotoResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoPersonalisationDto = buildPhotoPersonalisationDto()
): GeneratePhotoResubmissionTemplatePreviewDto =
    GeneratePhotoResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )

fun buildGenerateIdDocumentResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: IdDocumentPersonalisationDto = buildIdDocumentPersonalisationDto()
): GenerateIdDocumentResubmissionTemplatePreviewDto =
    GenerateIdDocumentResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType
    )
