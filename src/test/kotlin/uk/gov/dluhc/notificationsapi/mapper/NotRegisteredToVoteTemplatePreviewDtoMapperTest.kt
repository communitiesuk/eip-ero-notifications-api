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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateNotRegisteredToVoteTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildNotRegisteredToVotePersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotRegisteredToVotePersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildNotRegisteredToVoteTemplatePreviewDto
import java.time.LocalDate
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto

@ExtendWith(MockitoExtension::class)
class NotRegisteredToVoteTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var deadlineMapper: DeadlineMapper

    @InjectMocks
    private lateinit var mapper: NotRegisteredToVoteTemplatePreviewDtoMapperImpl

    @CommunicationChannelsTest
    fun `should map not registered to vote template preview request to dto`(channel: CommunicationChannel) {
        // Given
        val freeText = "Free text"
        val property = "1234"
        val street = "Fake Street"
        val town = "Fakehampton"
        val area = "Fakeshire"
        val locality = "Fakenham"
        val postcode = "FA1 2KE"
        val deadlineDate = LocalDate.of(2024, 7, 10)
        val deadlineTime = "17:00"

        val request = buildGenerateNotRegisteredToVoteTemplatePreviewRequest(
            channel = channel,
            personalisation = buildNotRegisteredToVotePersonalisation(
                freeText = freeText,
                property = property,
                street = street,
                town = town,
                area = area,
                locality = locality,
                postcode = postcode,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
            ),
        )
        val expectedChannel = CommunicationChannelDto.valueOf(channel.name)
        given { communicationChannelMapper.fromApiToDto(request.channel) }.willReturn(expectedChannel)
        given { sourceTypeMapper.fromApiToDto(SourceType.POSTAL) }.willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL)
        given { sourceTypeMapper.toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH) }.willReturn("postal vote")
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)
        given { deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, LanguageDto.ENGLISH, "postal vote") }.willReturn("Deadline string")

        val expected = buildNotRegisteredToVoteTemplatePreviewDto(
            sourceType = uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL,
            channel = expectedChannel,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildNotRegisteredToVotePersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    eroContactDetails = with(eroContactDetails) {
                        buildContactDetailsDto(
                            localAuthorityName = localAuthorityName,
                            website = website,
                            phone = phone,
                            email = email,
                            address =
                            buildAddressDto(
                                street = address.street,
                                property = address.property,
                                locality = address.locality,
                                town = address.town,
                                area = address.area,
                                postcode = address.postcode,
                            ),
                        )
                    },
                    freeText = freeText,
                    property = property,
                    street = street,
                    town = town,
                    area = area,
                    locality = locality,
                    postcode = postcode,
                    deadline = "Deadline string",
                )
            },
            notificationType = NotificationType.NOT_REGISTERED_TO_VOTE,
        )

        // When
        val actual = mapper.toDto(request)

        // Then
        Assertions.assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(sourceTypeMapper).fromApiToDto(SourceType.POSTAL)
        verify(sourceTypeMapper, times(2)).toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)
    }
}
