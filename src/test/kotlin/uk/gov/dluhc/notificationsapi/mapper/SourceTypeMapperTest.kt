package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

class SourceTypeMapperTest {
    private val mapper = SourceTypeMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_MINUS_CARD, VOTER_CARD",
            "POSTAL, POSTAL",
            "PROXY, PROXY"
        ]
    )
    fun `should map Message Source Type enum to DTO Source Type`(
        sourceType: SourceTypeMessageEnum,
        expected: SourceTypeDto
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
            "PROXY, PROXY"
        ]
    )
    fun `should map DTO Source Type to Entity Source Type`(
        dtoType: SourceTypeDto,
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
            "PROXY, PROXY"
        ]
    )
    fun `should map Entity Source Type to DTO Source Type`(
        entityType: SourceTypeEntityEnum,
        expected: SourceTypeDto
    ) {
        // Given

        // When
        val actual = mapper.fromEntityToDto(entityType)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "POSTAL, POSTAL",
            "VOTER_MINUS_CARD, VOTER_CARD",
            "PROXY, PROXY"
        ]
    )
    fun `should map api sourceType to sourceType dto`(
        apiSourceTypeEnum: SourceTypeModel,
        expected: SourceTypeDto
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiSourceTypeEnum)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
