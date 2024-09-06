package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVotePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.NotRegisteredToVoteTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildNotRegisteredToVoteTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    notificationType: NotificationType = NotificationType.NOT_REGISTERED_TO_VOTE,
    personalisation: NotRegisteredToVotePersonalisationDto = buildNotRegisteredToVotePersonalisationDto(),
): NotRegisteredToVoteTemplatePreviewDto =
    NotRegisteredToVoteTemplatePreviewDto(
        channel = channel,
        language = language,
        sourceType = sourceType,
        notificationType = notificationType,
        personalisation = personalisation,
    )
