package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class NinoNotMatchedTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @InjectMocks
    private lateinit var mapper: NinoNotMatchedTemplatePreviewDtoMapperImpl

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL",
            "LETTER",
        ]
    )
    fun `should map nino not matched template preview request to dto`(channel: NotificationChannel) {

        // Given
        val additionalNotes = "Invalid"
        val request = buildGenerateNinoNotMatchedTemplatePreviewRequest(
            channel = channel,
            personalisation = buildNinoNotMatchedPersonalisation(
                additionalNotes = additionalNotes
            )
        )
        given { notificationChannelMapper.fromApiToDto(request.channel) }.willReturn(
            NotificationChannelDto.valueOf(channel.name)
        )
        given { sourceTypeMapper.fromApiToDto(SourceType.POSTAL) }.willReturn(SourceTypeDto.POSTAL)
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)

        // When
        val mappedDto = mapper.toDto(request)

        // Then
        assertThat(mappedDto)
            .extracting("channel", "sourceType", "language")
            .containsExactly(
                NotificationChannelDto.valueOf(channel.name),
                SourceTypeDto.POSTAL,
                LanguageDto.ENGLISH,
            )

        assertThat(mappedDto.personalisation)
            .usingRecursiveComparison()
            .isEqualTo(request.personalisation)

        assertThat(mappedDto.personalisation.additionalNotes).isEqualTo(additionalNotes)
    }
}
