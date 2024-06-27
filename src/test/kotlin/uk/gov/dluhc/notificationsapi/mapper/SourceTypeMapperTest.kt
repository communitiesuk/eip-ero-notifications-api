package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.database.entity.SourceType as SourceTypeEntityEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessageEnum
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

class SourceTypeMapperTest {

    private val messageSource = ResourceBundleMessageSource().apply {
        setBasenames("messages")
        setDefaultEncoding("UTF-8")
        setFallbackToSystemLocale(true)
    }
    private val mapper = SourceTypeMapper(messageSource)

    @ParameterizedTest
    @CsvSource(
        value = [
            "VOTER_MINUS_CARD, VOTER_CARD",
            "POSTAL, POSTAL",
            "PROXY, PROXY",
            "OVERSEAS, OVERSEAS",
        ],
    )
    fun `should map Message Source Type enum to DTO Source Type`(
        sourceType: SourceTypeMessageEnum,
        expected: SourceTypeDto,
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
            "PROXY, PROXY",
            "OVERSEAS, OVERSEAS",
        ],
    )
    fun `should map DTO Source Type to Entity Source Type`(
        dtoType: SourceTypeDto,
        expected: SourceTypeEntityEnum,
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
            "PROXY, PROXY",
            "OVERSEAS, OVERSEAS",
        ],
    )
    fun `should map Entity Source Type to DTO Source Type`(
        entityType: SourceTypeEntityEnum,
        expected: SourceTypeDto,
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
            "PROXY, PROXY",
            "OVERSEAS, OVERSEAS",
        ],
    )
    fun `should map api sourceType to sourceType dto`(
        apiSourceTypeEnum: SourceTypeModel,
        expected: SourceTypeDto,
    ) {
        // Given

        // When
        val actual = mapper.fromApiToDto(apiSourceTypeEnum)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "OVERSEAS,overseas",
            "POSTAL,postal",
            "PROXY,proxy",
            "VOTER_MINUS_CARD,''",
        ],
    )
    fun `should map message sourceType to human readable messages in English`(
        sourceType: SourceTypeMessageEnum,
        expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toSourceTypeString(sourceType, LanguageDto.ENGLISH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "OVERSEAS,overseas",
            "POSTAL,postal",
            "PROXY,proxy",
            "VOTER_MINUS_CARD,''",
        ],
    )
    fun `should map API sourceType to human readable messages in English`(
        sourceType: SourceTypeModel,
        expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toSourceTypeString(sourceType, LanguageDto.ENGLISH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "OVERSEAS,''",
            "POSTAL,drwy'r post",
            "PROXY,drwy ddirprwy",
            "VOTER_MINUS_CARD,''",
        ],
    )
    fun `should map message sourceType to human readable messages in Welsh`(
        sourceType: SourceTypeMessageEnum,
        expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toSourceTypeString(sourceType, LanguageDto.WELSH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "OVERSEAS,''",
            "POSTAL,drwy'r post",
            "PROXY,drwy ddirprwy",
            "VOTER_MINUS_CARD,''",
        ],
    )
    fun `should map API sourceType to human readable messages in Welsh`(
        sourceType: SourceTypeModel,
        expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toSourceTypeString(sourceType, LanguageDto.WELSH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
            value = [
                "OVERSEAS,overseas vote",
                "POSTAL,postal vote",
                "PROXY,proxy vote",
                "VOTER_MINUS_CARD,Voter Authority Certificate",
            ],
    )
    fun `should map message sourceType to human readable full source in English`(
            sourceType: SourceTypeMessageEnum,
            expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toFullSourceTypeString(sourceType, LanguageDto.ENGLISH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
            value = [
                "OVERSEAS,bleidlais dramor",
                "POSTAL,bleidlais bost",
                "PROXY,bleidlais drwy ddirprwy",
                "VOTER_MINUS_CARD,Dystysgrif Awdurdod Pleidleisiwr",
            ],
    )
    fun `should map message sourceType to human readable full source in Welsh`(
            sourceType: SourceTypeMessageEnum,
            expected: String,
    ) {
        // Given

        // When
        val actual = mapper.toFullSourceTypeString(sourceType, LanguageDto.WELSH)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
