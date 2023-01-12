package uk.gov.dluhc.notificationsapi.testsupport

import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun getRandomEroId(): String = "${faker.address().city().lowercase()}-city-council"

fun getDifferentRandomEroId(refEroId: String): String {
    var differentEroId = getRandomEroId()
    while (refEroId == differentEroId) {
        differentEroId = getRandomEroId()
    }
    return differentEroId
}
