package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

fun buildGeneratePhotoResubmissionTemplatePreviewRequest(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    personalisation: PhotoPersonalisation = buildPhotoResubmissionPersonalisationRequest(),
    sourceType: SourceType,
): GeneratePhotoResubmissionTemplatePreviewRequest =
    GeneratePhotoResubmissionTemplatePreviewRequest(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
    )

fun buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    personalisation: IdDocumentPersonalisation = buildIdDocumentResubmissionPersonalisationRequest(),
    sourceType: SourceType,
): GenerateIdDocumentResubmissionTemplatePreviewRequest =
    GenerateIdDocumentResubmissionTemplatePreviewRequest(
        channel = channel,
        language = language,
        personalisation = personalisation,
        sourceType = sourceType,
    )
