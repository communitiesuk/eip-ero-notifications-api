package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateRequestedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDtoEnum
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDtoEnum

@ExtendWith(MockitoExtension::class)
class RequestedSignatureTemplatePreviewDtoMapperTest {
    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @InjectMocks
    private lateinit var mapper: RequestedSignatureTemplatePreviewDtoMapperImpl

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL",
            "LETTER",
        ]
    )
    fun `should map rejected signature template preview request to dto`(channel: NotificationChannel) {
        val request = buildGenerateRequestedSignatureTemplatePreviewRequest(
            channel = channel,
        )

        given(notificationChannelMapper.fromApiToDto(request.channel))
            .willReturn(NotificationChannelDtoEnum.valueOf(channel.name))
        given(sourceTypeMapper.fromApiToDto(SourceType.PROXY)).willReturn(SourceTypeDtoEnum.PROXY)
        given(languageMapper.fromApiToDto(Language.EN)).willReturn(LanguageDto.ENGLISH)

        val mappedDto = mapper.toRequestedSignatureTemplatePreviewDto(request)

        Assertions.assertThat(mappedDto).extracting("channel", "sourceType", "language").containsExactly(
            uk.gov.dluhc.notificationsapi.dto.NotificationChannel.valueOf(channel.name),
            uk.gov.dluhc.notificationsapi.dto.SourceType.PROXY,
            LanguageDto.ENGLISH,
        )
        Assertions.assertThat(mappedDto.personalisation)
            .usingRecursiveComparison()
            .isEqualTo(request.personalisation)
    }
}
