package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.GenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SignatureResubmissionPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType

fun buildGenerateSignatureResubmissionTemplatePreviewRequest(
    personalisation: SignatureResubmissionPersonalisation = buildSignatureResubmissionPersonalisation(),
    language: Language = Language.EN,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    sourceType: SourceType,
): GenerateSignatureResubmissionTemplatePreviewRequest =
    GenerateSignatureResubmissionTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        channel = channel,
        sourceType = sourceType,
    )
