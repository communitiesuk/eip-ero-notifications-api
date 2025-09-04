package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.GenerateSignatureResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.SignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType

fun buildGenerateSignatureResubmissionTemplatePreviewDto(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    sourceType: SourceType = SourceType.POSTAL,
    languageDto: LanguageDto = LanguageDto.ENGLISH,
    personalisation: SignatureResubmissionPersonalisationDto = buildSignatureResubmissionPersonalisationDto(),
    notificationType: NotificationType = NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS,
): GenerateSignatureResubmissionTemplatePreviewDto = GenerateSignatureResubmissionTemplatePreviewDto(
    channel = channel,
    sourceType = sourceType,
    language = languageDto,
    personalisation = personalisation,
    notificationType = notificationType,
)
