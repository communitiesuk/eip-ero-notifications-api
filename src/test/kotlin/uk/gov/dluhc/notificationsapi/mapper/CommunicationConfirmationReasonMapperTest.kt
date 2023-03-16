package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.CommunicationConfirmationReasonDto
import uk.gov.dluhc.notificationsapi.database.entity.CommunicationConfirmationReason as OfflineCommunicationReasonEntity
import uk.gov.dluhc.notificationsapi.models.OfflineCommunicationReason as OfflineCommunicationReasonApi

class CommunicationConfirmationReasonMapperTest {
    private val mapper = CommunicationConfirmationReasonMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_MINUS_REJECTED, APPLICATION_REJECTED",
            "PHOTO_MINUS_REJECTED, PHOTO_REJECTED",
            "DOCUMENT_MINUS_REJECTED, DOCUMENT_REJECTED",
        ]
    )
    fun `from api to dto`(apiEnum: OfflineCommunicationReasonApi, dtoEnum: CommunicationConfirmationReasonDto) {
        // Given

        // When
        val actualFromApiToDto = mapper.fromApiToDto(apiEnum)

        // Then
        assertThat(actualFromApiToDto).isEqualTo(dtoEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_REJECTED, APPLICATION_MINUS_REJECTED",
            "PHOTO_REJECTED, PHOTO_MINUS_REJECTED",
            "DOCUMENT_REJECTED, DOCUMENT_MINUS_REJECTED",
        ]
    )
    fun `from dto to api`(dtoEnum: CommunicationConfirmationReasonDto, apiEnum: OfflineCommunicationReasonApi) {
        // Given

        // When
        val actualFromDtoToApi = mapper.fromDtoToApi(dtoEnum)

        // Then
        assertThat(actualFromDtoToApi).isEqualTo(apiEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_REJECTED, APPLICATION_REJECTED",
            "PHOTO_REJECTED, PHOTO_REJECTED",
            "DOCUMENT_REJECTED, DOCUMENT_REJECTED",
        ]
    )
    fun `from entity to dto`(entityEnum: OfflineCommunicationReasonEntity, dtoEnum: CommunicationConfirmationReasonDto) {
        // Given

        // When
        val actualFromEntityToDto = mapper.fromEntityToDto(entityEnum)

        // Then
        assertThat(actualFromEntityToDto).isEqualTo(dtoEnum)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_REJECTED, APPLICATION_REJECTED",
            "PHOTO_REJECTED, PHOTO_REJECTED",
            "DOCUMENT_REJECTED, DOCUMENT_REJECTED",
        ]
    )
    fun `from dto to entity`(dtoEnum: CommunicationConfirmationReasonDto, entityEnum: OfflineCommunicationReasonEntity) {
        // Given

        // When
        val actualFromDtoToEntity = mapper.fromDtoToEntity(dtoEnum)

        // Then
        assertThat(actualFromDtoToEntity).isEqualTo(entityEnum)
    }
}
