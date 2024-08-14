package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.InviteToRegisterTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildInviteToRegisterTemplatePreviewDto(
    sourceType: SourceType,
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    notificationType: NotificationType = NotificationType.INVITE_TO_REGISTER,
    personalisation: InviteToRegisterPersonalisationDto = buildInviteToRegisterPersonalisationDto(),
): InviteToRegisterTemplatePreviewDto =
    InviteToRegisterTemplatePreviewDto(
        channel = channel,
        language = language,
        sourceType = sourceType,
        notificationType = notificationType,
        personalisation = personalisation,
    )
