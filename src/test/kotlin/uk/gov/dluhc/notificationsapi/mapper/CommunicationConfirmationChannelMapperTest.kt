package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationChannelDto
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
    fun fromApiToDto(apiEnum: OfflineCommunicationChannelApi, expectedDto: OfflineCommunicationChannelDto) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiEnum)

        // Then
        Assertions.assertThat(actual).isEqualTo(expectedDto)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun fromDtoToEntity(dto: OfflineCommunicationChannelDto, expectedEntity: OfflineCommunicationChannelEntity) {
        // Given

        // When
        val actual = mapper.fromDtoToEntity(dto)

        // Then
        Assertions.assertThat(actual).isEqualTo(expectedEntity)
    }
}
