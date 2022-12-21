package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

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
