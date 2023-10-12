package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.RequestedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildGenerateRequestedSignatureTemplatePreviewDto(
    sourceType: SourceType = SourceType.PROXY,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: RequestedSignaturePersonalisationDto = buildRequestedSignaturePersonalisationDto(),
) = RequestedSignatureTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    personalisation = personalisation,
    notificationType = NotificationType.REQUESTED_SIGNATURE,
)

fun buildRequestedSignaturePersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    freeText: String? = null,
    sourceType: String = "postal",
) = RequestedSignaturePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    freeText = freeText,
    sourceType = sourceType,
)
