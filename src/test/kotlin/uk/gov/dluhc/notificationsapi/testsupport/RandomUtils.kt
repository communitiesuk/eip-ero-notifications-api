package uk.gov.dluhc.notificationsapi.testsupport

import org.apache.commons.lang3.RandomStringUtils
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun getRandomEroId(): String = "${aValidEroName().lowercase().replace(Regex("[\\s']"), "-")}-city-council"

fun getDifferentRandomEroId(refEroId: String): String {
    var differentEroId = getRandomEroId()
    while (refEroId == differentEroId) {
        differentEroId = getRandomEroId()
    }
    return differentEroId
}

fun aValidEroName(): String = faker.address().city()

fun aValidLocalAuthorityName(): String = faker.address().city()

fun getRandomGssCode() = "E${RandomStringUtils.secure().nextNumeric(8)}"

fun aValidPhoneNumber(): String = faker.phoneNumber().cellPhone()

fun aValidEmailAddress(): String = "contact@${aValidEroName().replaceSpacesWith("-")}.gov.uk"

fun aValidWebsite(): String = "https://${aValidEroName().replaceSpacesWith("-")}.gov.uk"
