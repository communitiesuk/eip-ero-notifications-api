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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildBespokeCommPersonalisation
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateBespokeCommTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildBespokeCommPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildBespokeCommTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import java.time.LocalDate
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto

@ExtendWith(MockitoExtension::class)
class BespokeCommTemplatePreviewDtoMapperTest {

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var deadlineMapper: DeadlineMapper

    @InjectMocks
    private lateinit var mapper: BespokeCommTemplatePreviewDtoMapperImpl

    @CommunicationChannelsTest
    fun `should map bespoke comm template preview request to dto`(channel: CommunicationChannel) {
        // Given
        val subjectHeader = "Your postal vote"
        val details = "Postal vote details"
        val whatToDo = "What you need to do"
        val deadlineDate = LocalDate.of(2024, 7, 10)
        val deadlineTime = "17:00"

        val request = buildGenerateBespokeCommTemplatePreviewRequest(
            channel = channel,
            personalisation = buildBespokeCommPersonalisation(
                subjectHeader = subjectHeader,
                details = details,
                whatToDo = whatToDo,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
            ),
        )
        val expectedChannel = CommunicationChannelDto.valueOf(channel.name)
        given { communicationChannelMapper.fromApiToDto(request.channel) }.willReturn(expectedChannel)
        given { sourceTypeMapper.fromApiToDto(SourceType.POSTAL) }.willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL)
        given { sourceTypeMapper.toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH) }.willReturn("postal vote")
        given { languageMapper.fromApiToDto(Language.EN) }.willReturn(LanguageDto.ENGLISH)
        given(deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, LanguageDto.ENGLISH, "postal vote")).willReturn("Deadline string")

        val expected = buildBespokeCommTemplatePreviewDto(
            sourceType = uk.gov.dluhc.notificationsapi.dto.SourceType.POSTAL,
            channel = expectedChannel,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildBespokeCommPersonalisationDto(
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
                    subjectHeader = subjectHeader,
                    details = details,
                    whatToDo = whatToDo,
                    deadline = "Deadline string",
                    sourceType = "postal vote",
                )
            },
            notificationType = NotificationType.BESPOKE_COMM,
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
        verify(deadlineMapper).toDeadlineString(deadlineDate, deadlineTime, LanguageDto.ENGLISH, "postal vote")
    }
}
