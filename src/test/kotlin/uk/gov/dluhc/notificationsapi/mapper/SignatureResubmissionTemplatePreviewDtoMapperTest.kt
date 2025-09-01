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
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.annotations.CommunicationChannelsTest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.DataFaker.Companion.faker
import uk.gov.dluhc.notificationsapi.testsupport.testdata.aValidApplicationReference
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSignatureResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildSignatureResubmissionPersonalisation
import java.time.LocalDate
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
internal class SignatureResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: SignatureResubmissionTemplatePreviewDtoMapper

    @Mock
    private lateinit var deadlineMapper: DeadlineMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var communicationChannelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Mock
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Mock
    private lateinit var addressMapper: AddressMapper

    @CommunicationChannelsTest
    fun `should map signature resubmission template preview request to dto`(channel: CommunicationChannel) {
        // Given
        val applicationReference = aValidApplicationReference()
        val firstName = faker.name().firstName()
        val eroContactDetails = buildEroContactDetails()
        val deadlineDate = LocalDate.of(2024, 7, 10)
        val deadlineTime = "17:00"
        val uploadSignatureLink = "https://a-link.gov.uk/resubmit-signature"
        val rejectionNotes = "Rejection notes"
        val rejectionFreeText = "Free text"

        val request = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            channel = channel,
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionReasons = listOf(
                    SignatureRejectionReason.WRONG_MINUS_SIZE,
                ),
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroContactDetails,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
                uploadSignatureLink = uploadSignatureLink,
                rejectionNotes = rejectionNotes,
                rejectionFreeText = rejectionFreeText,
            ),
            sourceType = SourceType.POSTAL,
        )

        val expectedChannel = CommunicationChannelDto.valueOf(channel.name)
        val eroContactDetailsDto = buildContactDetailsDto()
        given(communicationChannelMapper.fromApiToDto(request.channel)).willReturn(expectedChannel)
        given(sourceTypeMapper.fromApiToDto(SourceType.POSTAL)).willReturn(SourceTypeDto.POSTAL)
        given(sourceTypeMapper.toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)).willReturn("postal vote")
        given(languageMapper.fromApiToDto(Language.EN)).willReturn(LanguageDto.ENGLISH)
        given(deadlineMapper.toDeadlineString(deadlineDate, deadlineTime, LanguageDto.ENGLISH, "postal vote")).willReturn("Deadline string")
        given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReason.WRONG_MINUS_SIZE, LanguageDto.ENGLISH)).willReturn("wrong size")
        given(eroContactDetailsMapper.fromApiToDto(request.personalisation.eroContactDetails)).willReturn(eroContactDetailsDto)

        val expected = buildSignatureResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.POSTAL,
            channel = expectedChannel,
            languageDto = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildSignatureResubmissionPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    eroContactDetails = eroContactDetailsDto,
                    sourceType = "postal vote",
                    freeText = rejectionFreeText,
                    rejectionNotes = rejectionNotes,
                    rejectionReasons = listOf("wrong size"),
                    uploadSignatureLink = uploadSignatureLink,
                    deadline = "Deadline string",
                )
            },
            notificationType = NotificationType.SIGNATURE_RESUBMISSION_WITH_REASONS,
        )

        // When
        val actual = mapper.toSignatureResubmissionTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected)
        verify(communicationChannelMapper).fromApiToDto(channel)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(sourceTypeMapper).fromApiToDto(SourceType.POSTAL)
        verify(sourceTypeMapper, times(2)).toFullSourceTypeString(SourceType.POSTAL, LanguageDto.ENGLISH)
        verify(deadlineMapper).toDeadlineString(deadlineDate, deadlineTime, LanguageDto.ENGLISH, "postal vote")
        verify(eroContactDetailsMapper).fromApiToDto(eroContactDetails)
        verify(addressMapper).fromApiToDto(eroContactDetails.address)
    }
}
