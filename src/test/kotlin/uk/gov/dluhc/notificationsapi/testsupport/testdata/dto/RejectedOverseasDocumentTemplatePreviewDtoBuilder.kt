package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.DocumentCategoryDto
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildRejectedOverseasDocumentTemplatePreviewDto(
    channel: CommunicationChannel = CommunicationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: RejectedOverseasDocumentPersonalisationDto = buildRejectedOverseasDocumentTemplatePreviewPersonalisation(),
    documentCategory: DocumentCategoryDto = DocumentCategoryDto.PARENT_GUARDIAN,
) = GenerateRejectedOverseasDocumentTemplatePreviewDto(
    channel = channel,
    language = language,
    documentCategory = documentCategory,
    personalisation = personalisation,
)

fun buildRejectedOverseasDocumentTemplatePreviewPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    rejectedDocumentFreeText: String? = DataFaker.faker.harryPotter().spell(),
    documents: List<String> = listOf(DataFaker.faker.lordOfTheRings().location()),
) = RejectedOverseasDocumentPersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectedDocumentFreeText = rejectedDocumentFreeText,
    documents = documents,
)
