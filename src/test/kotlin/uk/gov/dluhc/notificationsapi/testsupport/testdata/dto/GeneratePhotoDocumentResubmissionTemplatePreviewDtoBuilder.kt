package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.PhotoResubmissionPersonalisationDto

fun buildGeneratePhotoResubmissionTemplatePreviewDto(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoResubmissionPersonalisationDto = buildPhotoResubmissionPersonalisationDto()
): GeneratePhotoResubmissionTemplatePreviewDto =
    GeneratePhotoResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )

fun buildGenerateIdDocumentResubmissionTemplatePreviewDto(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoResubmissionPersonalisationDto = buildPhotoResubmissionPersonalisationDto()
): GenerateIdDocumentResubmissionTemplatePreviewDto =
    GenerateIdDocumentResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )
