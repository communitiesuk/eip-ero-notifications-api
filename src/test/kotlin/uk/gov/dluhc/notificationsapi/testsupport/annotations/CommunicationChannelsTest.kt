package uk.gov.dluhc.notificationsapi.testsupport.annotations

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel

@ParameterizedTest
@EnumSource(value = CommunicationChannel::class, names = ["EMAIL", "LETTER"])
annotation class CommunicationChannelsTest
