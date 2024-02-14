package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.GenerateParentGuardianRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.ParentGuardianPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildParentGauardianRequiredTemplatePreviewDto(
    sourceType: SourceType = SourceType.PROXY,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: ParentGuardianPersonalisationDto = buildParentGuardianRequiredTemplatePreviewPersonalisation(),
) = GenerateParentGuardianRequiredTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    personalisation = personalisation,
)

fun buildParentGuardianRequiredTemplatePreviewPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    freeText: String? = null,
    sourceType: String = "postal",
) = ParentGuardianPersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    freeText = freeText,
)
