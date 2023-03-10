package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.OfflineCommunicationReasonDto
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
    fun fromApiToDto(apiEnum: OfflineCommunicationReasonApi, expectedDto: OfflineCommunicationReasonDto) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiEnum)

        // Then
        Assertions.assertThat(actual).isEqualTo(expectedDto)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "APPLICATION_REJECTED, APPLICATION_REJECTED",
            "PHOTO_REJECTED, PHOTO_REJECTED",
            "DOCUMENT_REJECTED, DOCUMENT_REJECTED",
        ]
    )
    fun fromDtoToEntity(dto: OfflineCommunicationReasonDto, expectedEntity: OfflineCommunicationReasonEntity) {
        // Given

        // When
        val actual = mapper.fromDtoToEntity(dto)

        // Then
        Assertions.assertThat(actual).isEqualTo(expectedEntity)
    }
}
