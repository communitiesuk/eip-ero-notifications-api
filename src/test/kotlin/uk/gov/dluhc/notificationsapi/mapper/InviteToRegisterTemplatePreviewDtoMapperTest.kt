package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.annotations.CommunicationChannelsTest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateInviteToRegisterTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildInviteToRegisterPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildInviteToRegisterPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildInviteToRegisterTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto

@ExtendWith(MockitoExtension::class)
class InviteToRegisterTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @InjectMocks
    private lateinit var mapper: InviteToRegisterTemplatePreviewDtoMapperImpl

    @CommunicationChannelsTest
    fun `should map invite to register template preview request to dto`(channel: CommunicationChannel) {
        // Given
        val freeText = "Free text"
        val property = "1234"
        val street = "Fake Street"
        val town = "Fakehampton"
        val area = "Fakeshire"
        val locality = "Fakenham"
        val postcode = "FA1 2KE"

        val request = buildGenerateInviteToRegisterTemplatePreviewRequest(
            channel = channel,
            personalisation = buildInviteToRegisterPersonalisation(
                freeText = freeText,
                property = property,
                street = street,
                town = town,
                area = area,
                locality = locality,
                postcode = postcode,
            ),
        )
        val expectedChannel = CommunicationChannelDto.valueOf(channel.name)
        given { communicationChannelMapper.fromApiToDto(request.channel) }.willReturn(expectedChannel)
        given { sourceTypeMapper.fromApiToDto(SourceType.POSTAL) }.willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL)
        given { sourceTypeMapper.toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH) }.willReturn("postal vote")
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)

        val expected = buildInviteToRegisterTemplatePreviewDto(
            sourceType = uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL,
            channel = expectedChannel,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildInviteToRegisterPersonalisationDto(
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
                    freeText = freeText,
                    property = property,
                    street = street,
                    town = town,
                    area = area,
                    locality = locality,
                    postcode = postcode,
                )
            },
            notificationType = NotificationType.INVITE_TO_REGISTER,
        )

        // When
        val actual = mapper.toDto(request)

        // Then
        Assertions.assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(sourceTypeMapper).fromApiToDto(SourceType.POSTAL)
        verify(sourceTypeMapper, times(1)).toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)
    }
}
