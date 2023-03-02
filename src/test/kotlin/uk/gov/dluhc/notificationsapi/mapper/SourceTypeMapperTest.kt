package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum

class SourceTypeMapperTest {
    private val mapper = SourceTypeMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_MINUS_CARD, VOTER_CARD",
        ]
    )
    fun `should map Message Source Type enum to DTO Source Type`(
        sourceType: SourceTypeMessageEnum,
        expected: SourceType
    ) {
        // Given

        // When
        val actual = mapper.fromMessageToDto(sourceType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD, VOTER_CARD",
            "POSTAL, POSTAL",
        ]
    )
    fun `should map DTO Source Type to Entity Source Type`(
        dtoType: SourceType,
        expected: SourceTypeEntityEnum
    ) {
        // Given

        // When
        val actual = mapper.fromDtoToEntity(dtoType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD, VOTER_CARD",
            "POSTAL, POSTAL",
        ]
    )
    fun `should map Entity Source Type to DTO Source Type`(
        entityType: SourceTypeEntityEnum,
        expected: SourceType
    ) {
        // Given

        // When
        val actual = mapper.fromEntityToDto(entityType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
