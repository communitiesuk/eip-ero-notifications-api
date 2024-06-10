package uk.gov.dluhc.notificationsapi.testsupport.testdata.api

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.GeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.PhotoPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

fun buildGeneratePhotoResubmissionTemplatePreviewRequest(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
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
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
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
