package uk.gov.dluhc.notificationsapi.testsupport.testdata.models

import uk.gov.dluhc.notificationsapi.models.Address
import uk.gov.dluhc.notificationsapi.models.ContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.aValidEmailAddress
import uk.gov.dluhc.notificationsapi.testsupport.aValidLocalAuthorityName
import uk.gov.dluhc.notificationsapi.testsupport.aValidPhoneNumber
import uk.gov.dluhc.notificationsapi.testsupport.aValidWebsite

fun buildEroContactDetails(
    localAuthorityName: String = aValidLocalAuthorityName(),
    website: String = aValidWebsite(),
    email: String = aValidEmailAddress(),
    address: Address = buildAddress(),
    phone: String = aValidPhoneNumber()
): ContactDetails =
    ContactDetails(
        localAuthorityName = localAuthorityName,
        website = website,
        email = email,
        address = address,
        phone = phone
    )
