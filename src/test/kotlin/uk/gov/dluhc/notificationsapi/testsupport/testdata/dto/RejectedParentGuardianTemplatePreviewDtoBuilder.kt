package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedParentGuardianTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.RejectedParentGuardianPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildRejectedParentGuardianTemplatePreviewDto(
    sourceType: SourceType = SourceType.OVERSEAS,
    channel: NotificationChannel = NotificationChannel.EMAIL,
    language: LanguageDto = LanguageDto.ENGLISH,
    personalisation: RejectedParentGuardianPersonalisationDto = buildRejectedParentGuardianTemplatePreviewPersonalisation(),
) = GenerateRejectedParentGuardianTemplatePreviewDto(
    sourceType = sourceType,
    channel = channel,
    language = language,
    personalisation = personalisation,
)

fun buildRejectedParentGuardianTemplatePreviewPersonalisation(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    rejectedDocumentFreeText: String? = DataFaker.faker.harryPotter().spell(),
    documents: List<String> = listOf(DataFaker.faker.lordOfTheRings().location()),
) = RejectedParentGuardianPersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectedDocumentFreeText = rejectedDocumentFreeText,
    documents = documents,
)
