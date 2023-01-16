package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.EroContactDetailsDto
import uk.gov.dluhc.notificationsapi.dto.EroDto

fun buildEroDto(
    englishContactDetails: EroContactDetailsDto = anEnglishEroContactDetails(),
    welshContactDetails: EroContactDetailsDto? = aWelshEroContactDetails()
) = EroDto(
    englishContactDetails = englishContactDetails,
    welshContactDetails = welshContactDetails,
)
