package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
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
        val actual = mapper.toSourceTypeDto(sourceType)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_CARD, VOTER_CARD",
        ]
    )
    fun `should map DTO Source Type to Entity Source Type`(
        dtoType: SourceType,
        expected: SourceTypeEntityEnum
    ) {
        // Given

        // When
        val actual = mapper.toSourceTypeEntity(dtoType)

        // Then
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}
