package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.PhotoResubmissionPersonalisation

fun buildGeneratePhotoResubmissionTemplatePreviewRequest(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    personalisation: PhotoResubmissionPersonalisation = buildPhotoResubmissionPersonalisationRequest()
): GeneratePhotoResubmissionTemplatePreviewRequest =
    GeneratePhotoResubmissionTemplatePreviewRequest(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )

fun buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: Language = Language.EN,
    personalisation: PhotoResubmissionPersonalisation = buildPhotoResubmissionPersonalisationRequest()
): GenerateIdDocumentResubmissionTemplatePreviewRequest =
    GenerateIdDocumentResubmissionTemplatePreviewRequest(
        channel = channel,
        language = language,
        personalisation = personalisation,
    )
