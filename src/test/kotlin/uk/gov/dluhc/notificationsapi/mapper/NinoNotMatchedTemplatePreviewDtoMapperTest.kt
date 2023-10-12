package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedTemplatePreviewDto
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
        val expectedChannel = NotificationChannelDto.valueOf(channel.name)
        given { notificationChannelMapper.fromApiToDto(request.channel) }.willReturn(expectedChannel)
        given { sourceTypeMapper.fromApiToDto(SourceType.POSTAL) }.willReturn(SourceTypeDto.POSTAL)
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)
        given(sourceTypeMapper.toSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)).willReturn("Mapped source type")

        val expected = buildNinoNotMatchedTemplatePreviewDto(
            sourceType = SourceTypeDto.POSTAL,
            channel = expectedChannel,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildNinoNotMatchedPersonalisationDto(
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
                    additionalNotes = "Invalid",
                    sourceType = "Mapped source type",
                )
            }
        )

        // When
        val actual = mapper.toDto(request)

        // Then
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(sourceTypeMapper).fromApiToDto(SourceType.POSTAL)
        verify(sourceTypeMapper).toSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)
    }
}
