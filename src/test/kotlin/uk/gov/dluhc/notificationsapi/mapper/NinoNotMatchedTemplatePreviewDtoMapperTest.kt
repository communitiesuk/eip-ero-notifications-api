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
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateNinoNotMatchedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildNinoNotMatchedPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNinoNotMatchedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class NinoNotMatchedTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @InjectMocks
    private lateinit var mapper: NinoNotMatchedTemplatePreviewDtoMapperImpl

    @ParameterizedTest
    @CsvSource(
        value = [
            "EMAIL,false,NINO_NOT_MATCHED",
            "LETTER,false,NINO_NOT_MATCHED",
            "EMAIL,true,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
            "LETTER,true,NINO_NOT_MATCHED_RESTRICTED_DOCUMENTS_LIST",
        ],
    )
    fun `should map nino not matched template preview request to dto`(
        channel: CommunicationChannel,
        hasRestrictedDocumentsList: Boolean,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val additionalNotes = "Invalid"
        val request = buildGenerateNinoNotMatchedTemplatePreviewRequest(
            channel = channel,
            personalisation = buildNinoNotMatchedPersonalisation(
                additionalNotes = additionalNotes,
            ),
            hasRestrictedDocumentsList = hasRestrictedDocumentsList,
        )
        val expectedChannel = CommunicationChannelDto.valueOf(channel.name)
        given { communicationChannelMapper.fromApiToDto(request.channel) }.willReturn(expectedChannel)
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
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    additionalNotes = "Invalid",
                    sourceType = "Mapped source type",
                )
            },
            notificationType = expectedNotificationType,
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
