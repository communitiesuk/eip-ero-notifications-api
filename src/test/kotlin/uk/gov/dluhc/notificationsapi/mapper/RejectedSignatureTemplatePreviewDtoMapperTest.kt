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
import uk.gov.dluhc.notificationsapi.dto.RejectedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateRejectedSignatureTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildRejectedSignaturePersonalisation
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class RejectedSignatureTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @InjectMocks
    private lateinit var mapper: RejectedSignatureTemplatePreviewDtoMapperImpl

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL",
            "LETTER",
        ]
    )
    fun `should map rejected signature template preview request to dto`(channel: NotificationChannel) {
        val rejectionReasons = listOf("Invalid")
        val mappedDto = validate(channel, rejectionNotes = "Invalid signature", rejectionReasons = rejectionReasons)
        assertThat(mappedDto.personalisation.rejectionReasons).isEqualTo(rejectionReasons)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL",
            "LETTER",
        ]
    )
    fun `should map rejected signature template preview request with optional fields to dto `(channel: NotificationChannel) {
        val mappedDto = validate(channel)
        assertThat(mappedDto.personalisation.rejectionReasons).isEmpty()
    }

    private fun validate(
        channel: NotificationChannel,
        rejectionNotes: String? = null,
        rejectionReasons: List<String>? = null
    ): RejectedSignatureTemplatePreviewDto {
        val request = buildGenerateRejectedSignatureTemplatePreviewRequest(
            channel = channel,
            personalisation = buildRejectedSignaturePersonalisation(
                rejectionNotes = rejectionNotes, rejectionReasons = rejectionReasons
            )
        )
        given { notificationChannelMapper.fromApiToDto(request.channel) }.willReturn(
            NotificationChannelDto.valueOf(
                channel.name
            )
        )
        given { sourceTypeMapper.fromApiToDto(SourceType.PROXY) }.willReturn(SourceTypeDto.PROXY)
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)

        val mappedDto = mapper.toRejectedSignatureTemplatePreviewDto(request)

        assertThat(mappedDto).extracting("channel", "sourceType", "language").containsExactly(
            NotificationChannelDto.valueOf(channel.name),
            SourceTypeDto.PROXY,
            LanguageDto.ENGLISH,
        )
        with(mappedDto.personalisation) {
            assertThat(this).usingRecursiveComparison()
                .ignoringFields("rejectionReasons").isEqualTo(request.personalisation)
        }
        return mappedDto
    }
}
