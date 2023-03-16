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
    fun `from api to dto`(apiEnum: OfflineCommunicationChannelApi, dtoEnum: CommunicationConfirmationChannelDto) {
        // Given

        // When
        val actualFromApiToDto = mapper.fromApiToDto(apiEnum)

        // Then
        assertThat(actualFromApiToDto).isEqualTo(dtoEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun `from dto to api`(dtoEnum: CommunicationConfirmationChannelDto, apiEnum: OfflineCommunicationChannelApi) {
        // Given

        // When
        val actualFromDtoToApi = mapper.fromDtoToApi(dtoEnum)

        // Then
        assertThat(actualFromDtoToApi).isEqualTo(apiEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun `from entity to dto`(entityEnum: OfflineCommunicationChannelEntity, dtoEnum: CommunicationConfirmationChannelDto) {
        // Given

        // When
        val actualFromEntityToDto = mapper.fromEntityToDto(entityEnum)

        // Then
        assertThat(actualFromEntityToDto).isEqualTo(dtoEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL, EMAIL",
            "LETTER, LETTER",
            "TELEPHONE, TELEPHONE",
        ]
    )
    fun `from dto to entity`(dtoEnum: CommunicationConfirmationChannelDto, entityEnum: OfflineCommunicationChannelEntity) {
        // Given

        // When
        val actualFromDtoToEntity = mapper.fromDtoToEntity(dtoEnum)

        // Then
        assertThat(actualFromDtoToEntity).isEqualTo(entityEnum)
    }
}
