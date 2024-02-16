package uk.gov.dluhc.notificationsapi.dto.api

import uk.gov.dluhc.notificationsapi.dto.BaseGenerateTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.QualifyingAddressPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

class GenerateQualifyingAddressRequiredTemplatePreviewDto(
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel,
    language: LanguageDto,
    val personalisation: QualifyingAddressPersonalisationDto
) : BaseGenerateTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    notificationType = NotificationType.QUALIFYING_ADDRESS_REQUIRED
)
