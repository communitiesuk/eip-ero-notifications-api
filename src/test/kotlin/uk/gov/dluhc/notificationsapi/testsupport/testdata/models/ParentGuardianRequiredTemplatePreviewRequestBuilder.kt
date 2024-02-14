package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.BasePersonalisation
import uk.gov.dluhc.notificationsapi.models.GenerateParentGuardianRequiredTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType

fun buildParentGuardianTemplatePreviewRequest(
    personalisation: BasePersonalisation = buildBasePersonalisation(),
    language: Language = Language.EN,
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel = NotificationChannel.EMAIL
): GenerateParentGuardianRequiredTemplatePreviewRequest =
    GenerateParentGuardianRequiredTemplatePreviewRequest(
        personalisation = personalisation,
        language = language,
        sourceType = sourceType,
        channel = channel
    )