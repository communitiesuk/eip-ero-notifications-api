package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommonTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildCommonTemplatePreviewDto(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    sourceType: SourceType = SourceType.POSTAL,
    language: LanguageDto = LanguageDto.ENGLISH,
    notificationType: NotificationType = NotificationType.SIGNATURE_RESUBMISSION,
) = CommonTemplatePreviewDto(
    channel = channel,
    sourceType = sourceType,
    language = language,
    notificationType = notificationType,
)
