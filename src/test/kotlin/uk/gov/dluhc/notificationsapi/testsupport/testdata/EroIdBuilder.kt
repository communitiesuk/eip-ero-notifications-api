package uk.gov.dluhc.notificationsapi.testsupport.testdata

import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker

fun aValidKnownEroId() = "berkshire-county-council"

fun anotherValidKnownEroId() = "west-suffolk-council"

fun aValidRandomEroId() = "${faker.address().city().lowercase()}-city-council"
