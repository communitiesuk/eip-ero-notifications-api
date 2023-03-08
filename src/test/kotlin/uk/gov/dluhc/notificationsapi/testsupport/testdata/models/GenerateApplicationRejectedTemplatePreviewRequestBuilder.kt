package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.ApplicationRejectedPersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateApplicationRejectedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.Language.EN
import uk.gov.dluhc.notificationsapi.models.SourceType

fun buildGenerateApplicationRejectedTemplatePreviewRequest(
    personalisation: ApplicationRejectedPersonalisation = buildApplicationRejectedPersonalisation(),
    language: Language = EN,
    sourceType: SourceType
): GenerateApplicationRejectedTemplatePreviewRequest =
    GenerateApplicationRejectedTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType
    )
