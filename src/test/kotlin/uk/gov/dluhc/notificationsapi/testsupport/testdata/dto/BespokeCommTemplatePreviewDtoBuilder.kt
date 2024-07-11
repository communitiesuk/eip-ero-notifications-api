package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.BespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.BespokeCommTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildBespokeCommTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    notificationType: NotificationType = NotificationType.BESPOKE_COMM,
    personalisation: BespokeCommPersonalisationDto = buildBespokeCommPersonalisationDto(),
): BespokeCommTemplatePreviewDto =
    BespokeCommTemplatePreviewDto(
        channel = channel,
        language = language,
        sourceType = sourceType,
        notificationType = notificationType,
        personalisation = personalisation,
    )
