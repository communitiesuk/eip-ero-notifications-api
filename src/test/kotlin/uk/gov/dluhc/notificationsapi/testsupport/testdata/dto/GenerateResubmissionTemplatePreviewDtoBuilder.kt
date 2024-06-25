package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.GeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.PhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGeneratePhotoResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: PhotoPersonalisationDto = buildPhotoPersonalisationDto(),
    notificationType: NotificationType = NotificationType.PHOTO_RESUBMISSION,
): GeneratePhotoResubmissionTemplatePreviewDto =
    GeneratePhotoResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
        notificationType = notificationType,
    )

fun buildGenerateIdDocumentResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: IdDocumentPersonalisationDto = buildIdDocumentPersonalisationDto(),
    notificationType: NotificationType = NotificationType.ID_DOCUMENT_RESUBMISSION,
): GenerateIdDocumentResubmissionTemplatePreviewDto =
    GenerateIdDocumentResubmissionTemplatePreviewDto(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
        notificationType = notificationType,
    )
