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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateRequestedSignatureTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildRequestedSignaturePersonalisationDto
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

        val expectedChannel = NotificationChannelDtoEnum.valueOf(channel.name)
        given(notificationChannelMapper.fromApiToDto(request.channel)).willReturn(expectedChannel)
        given(sourceTypeMapper.fromApiToDto(SourceType.PROXY)).willReturn(SourceTypeDtoEnum.PROXY)
        given(languageMapper.fromApiToDto(Language.EN)).willReturn(LanguageDto.ENGLISH)
        given(sourceTypeMapper.toSourceTypeString(SourceType.PROXY, LanguageDto.ENGLISH)).willReturn("Mapped source type")

        val expected = buildGenerateRequestedSignatureTemplatePreviewDto(
            sourceType = SourceTypeDtoEnum.PROXY,
            channel = expectedChannel,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildRequestedSignaturePersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    eroContactDetails = with(eroContactDetails) {
                        buildContactDetailsDto(
                            localAuthorityName = localAuthorityName,
                            website = website,
                            phone = phone,
                            email = email,
                            address = with(address) {
                                buildAddressDto(
                                    street = street,
                                    property = property,
                                    locality = locality,
                                    town = town,
                                    area = area,
                                    postcode = postcode
                                )
                            }
                        )
                    },
                    freeText = freeText,
                    sourceType = "Mapped source type",
                )
            }
        )

        val actual = mapper.toRequestedSignatureTemplatePreviewDto(request)

        Assertions.assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
    }
}
