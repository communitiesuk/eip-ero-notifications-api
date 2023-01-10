package uk.gov.dluhc.notificationsapi.testsupport.testdata.dto

import uk.gov.dluhc.notificationsapi.dto.NotificationDestinationDto
import uk.gov.dluhc.notificationsapi.dto.PostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aPostalAddress
import uk.gov.dluhc.notificationsapi.testsupport.testdata.anEmailAddress

fun aNotificationDestination(
    emailAddress: String? = anEmailAddress(),
    postalAddress: PostalAddress = aPostalAddress()
): NotificationDestinationDto =
    NotificationDestinationDto(
        emailAddress = emailAddress,
        postalAddress = postalAddress
    )
