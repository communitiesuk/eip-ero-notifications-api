package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationChannelDto
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationChannel as OfflineCommunicationChannelEntity
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationChannel as OfflineCommunicationChannelApi

class CommunicationConfirmationChannelMapperTest {
    private val mapper = CommunicationConfirmationChannelMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun fromApiToDto(apiEnum: OfflineCommunicationChannelApi, expectedDto: CommunicationConfirmationChannelDto) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiEnum)

        // Then
        assertThat(actual).isEqualTo(expectedDto)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun fromDtoToEntity(dto: CommunicationConfirmationChannelDto, expectedEntity: OfflineCommunicationChannelEntity) {
        // Given

        // When
        val actual = mapper.fromDtoToEntity(dto)

        // Then
        assertThat(actual).isEqualTo(expectedEntity)
    }
}
