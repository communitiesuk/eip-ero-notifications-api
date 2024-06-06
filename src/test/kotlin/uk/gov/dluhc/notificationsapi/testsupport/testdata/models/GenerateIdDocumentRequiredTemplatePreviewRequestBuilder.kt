package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.GenerateIdDocumentRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.IdDocumentRequiredPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.Language.EN
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.models.SourceType.VOTER_MINUS_CARD

fun buildGenerateIdDocumentRequiredTemplatePreviewRequest(
    personalisation: IdDocumentRequiredPersonalisation = buildIdDocumentRequiredPersonalisation(),
    language: Language = EN,
    sourceType: SourceType = VOTER_MINUS_CARD,
    channel: NotificationChannel = EMAIL,
): GenerateIdDocumentRequiredTemplatePreviewRequest =
    GenerateIdDocumentRequiredTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel,
    )
