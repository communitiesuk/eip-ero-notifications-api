package uk.gov.dluhc.notificationsapi.testsupport.annotations

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.models.SourceType

@ParameterizedTest
@EnumSource(value = SourceType::class, names = ["POSTAL", "PROXY", "OVERSEAS", "VOTER_MINUS_CARD"])
annotation class SourceTypesApiEnumTest
