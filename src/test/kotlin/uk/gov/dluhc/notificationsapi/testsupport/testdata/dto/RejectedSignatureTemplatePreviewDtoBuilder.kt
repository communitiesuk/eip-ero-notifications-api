package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.RejectedSignaturePersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference

fun buildGenerateRejectedSignatureTemplatePreviewDto(
    channel: NotificationChannel = NotificationChannel.EMAIL,
    personalisation: RejectedSignaturePersonalisationDto = buildRejectedSignaturePersonalisationDto()
) = RejectedSignatureTemplatePreviewDto(
    channel = channel,
    sourceType = SourceType.PROXY,
    language = LanguageDto.ENGLISH,
    personalisation = personalisation
)

fun buildRejectedSignaturePersonalisationDto(
    applicationReference: String = aValidApplicationReference(),
    firstName: String = DataFaker.faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    rejectionNotes: String? = null,
    rejectionReasons: List<String> = emptyList()
) = RejectedSignaturePersonalisationDto(
    applicationReference = applicationReference,
    firstName = firstName,
    eroContactDetails = eroContactDetails,
    rejectionNotes = rejectionNotes,
    rejectionReasons = rejectionReasons
)