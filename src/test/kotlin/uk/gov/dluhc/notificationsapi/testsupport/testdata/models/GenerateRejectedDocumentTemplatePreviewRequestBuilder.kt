package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.GenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.Language.EN
import uk.gov.dluhc.notificationsapi.models.RejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.models.SourceType.POSTAL

fun buildGenerateRejectedDocumentTemplatePreviewRequest(
    personalisation: RejectedDocumentPersonalisation = buildRejectedDocumentPersonalisation(),
    language: Language = EN,
    sourceType: SourceType = POSTAL,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
): GenerateRejectedDocumentTemplatePreviewRequest =
    GenerateRejectedDocumentTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel,
    )
