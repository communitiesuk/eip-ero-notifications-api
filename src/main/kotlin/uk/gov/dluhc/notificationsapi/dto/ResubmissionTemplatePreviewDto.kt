package uk.gov.dluhc.notificationsapi.dto

class GenerateIdDocumentResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: IdDocumentPersonalisationDto,
    notificationType: NotificationType,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)

class GeneratePhotoResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: PhotoPersonalisationDto,
    notificationType: NotificationType,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)

class GenerateSignatureResubmissionTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel,
    language: LanguageDto,
    val personalisation: SignatureResubmissionPersonalisationDto,
    notificationType: NotificationType,
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = notificationType,
)
