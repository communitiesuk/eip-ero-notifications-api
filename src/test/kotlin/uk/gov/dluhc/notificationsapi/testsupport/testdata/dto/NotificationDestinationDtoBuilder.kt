package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.OverseasElectorAddress
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anOverseasAddress

fun aNotificationDestination(
    emailAddress: String? = anEmailAddress(),
    postalAddress: PostalAddress? = aPostalAddress(),
    overseasAddress: OverseasElectorAddress? = anOverseasAddress()
): NotificationDestinationDto =
    NotificationDestinationDto(
        emailAddress = emailAddress,
        postalAddress = postalAddress,
        overseasElectorAddress = overseasAddress
    )
