package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.GenerateRequiredOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.OverseasDocumentTypeDto
import uk.gov.dluhc.notificationsapi.dto.RequiredOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildRequiredOverseasDocumentTemplatePreviewDto(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: RequiredOverseasDocumentPersonalisationDto = buildRequiredOverseasDocumentTemplatePreviewPersonalisation(),
    overseasDocumentType: OverseasDocumentTypeDto = OverseasDocumentTypeDto.PARENT_GUARDIAN
) = GenerateRequiredOverseasDocumentTemplatePreviewDto(
    channel = channel,
    language = language,
    overseasDocumentType = overseasDocumentType,
    personalisation = personalisation,
)

fun buildRequiredOverseasDocumentTemplatePreviewPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(address = buildAddressDto(country = "Spain")),
    requiredDocumentFreeText: String? = DataFaker.faker.harryPotter().spell(),
) = RequiredOverseasDocumentPersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    requiredDocumentFreeText = requiredDocumentFreeText,
)
