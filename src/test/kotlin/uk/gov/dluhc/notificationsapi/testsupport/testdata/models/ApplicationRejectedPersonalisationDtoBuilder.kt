package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto

fun buildApplicationRejectedPersonalisationDto(
    rejectionReasonList: List<String> = listOf(
        "Photo does not meet criteria",
        "Suspected fraudulent application",
        "Application contains inaccurate information",
    ),
    applicationReference: String = aValidApplicationReference(),
    firstName: String = faker.name().firstName(),
    eroContactDetails: ContactDetailsDto = buildContactDetailsDto(),
    rejectionReasonMessage: String? = "Application rejected for inaccurate information",
): ApplicationRejectedPersonalisationDto =
    ApplicationRejectedPersonalisationDto(
        rejectionReasonList = rejectionReasonList,
        applicationReference = applicationReference,
        firstName = firstName,
        eroContactDetails = eroContactDetails,
        rejectionReasonMessage = rejectionReasonMessage,
    )
