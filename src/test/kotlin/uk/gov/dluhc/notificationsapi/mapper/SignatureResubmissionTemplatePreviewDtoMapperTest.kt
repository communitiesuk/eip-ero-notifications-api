package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSignatureResubmissionPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildSignatureResubmissionPersonalisationMapFromDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateSignatureResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildSignatureResubmissionPersonalisation
import java.time.LocalDate

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
    private lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Mock
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Mock
    private lateinit var templatePersonalisationDtoMapper: TemplatePersonalisationDtoMapper

    @Test
    fun `should map to personalisation map`() {
        // Given
        val personalisationDto = buildSignatureResubmissionPersonalisationDto(
            rejectionNotes = null,
            freeText = null,
            deadline = null,
        )

        given(templatePersonalisationDtoMapper.getSafeValue(null)).willReturn("")

        val expected = buildSignatureResubmissionPersonalisationMapFromDto(personalisationDto)

        // When
        val actual = mapper.toSignatureResubmissionPersonalisation(personalisationDto)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["true,SIGNATURE_RESUBMISSION_WITH_REASONS", "false,SIGNATURE_RESUBMISSION"],
    )
    fun `should map to correct notification type given rejection reasons`(
        shouldHaveRejectionReasons: Boolean,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionReasons = if (shouldHaveRejectionReasons) listOf(SignatureRejectionReason.WRONG_MINUS_SIZE) else emptyList(),
                rejectionNotes = if (shouldHaveRejectionReasons) "Rejection notes" else null,
            ),
            sourceType = SourceType.POSTAL,
        )

        // Then
        assertEquals(expectedNotificationType, mapper.signatureResubmissionNotificationType(request))
    }

    @Test
    fun `should map from request to personalisation dto`() {
        // Given
        val request = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            sourceType = SourceType.POSTAL,
            language = Language.EN,
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionNotes = "Notes",
                rejectionReasons = listOf(SignatureRejectionReason.WRONG_MINUS_SIZE, SignatureRejectionReason.HAS_MINUS_SHADOWS, SignatureRejectionReason.OTHER),
                rejectionFreeText = "Free Text",
                deadlineDate = LocalDate.of(2024, 10, 1),
            ),
        )

        val eroContactDetailsDto = buildContactDetailsDto()
        val sourceTypeString = "Source Type"
        val wrongSizeText = "Wrong Size"
        val hasShadowsText = "Has Shadows"
        val deadlineString = "Deadline"

        given(languageMapper.fromApiToDto(request.language!!)).willReturn(LanguageDto.ENGLISH)
        with(request.personalisation) {
            given(eroContactDetailsMapper.fromApiToDto(eroContactDetails)).willReturn(eroContactDetailsDto)
            given(sourceTypeMapper.toSourceTypeString(request.sourceType, LanguageDto.ENGLISH)).willReturn(sourceTypeString)
            given(sourceTypeMapper.toFullSourceTypeString(request.sourceType, LanguageDto.ENGLISH)).willReturn("Full String")
            given(deadlineMapper.toDeadlineString(deadlineDate!!, deadlineTime, LanguageDto.ENGLISH, "Full String")).willReturn(deadlineString)
        }
        given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReason.WRONG_MINUS_SIZE, LanguageDto.ENGLISH)).willReturn(wrongSizeText)
        given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReason.HAS_MINUS_SHADOWS, LanguageDto.ENGLISH)).willReturn(hasShadowsText)

        val expected = with(request.personalisation) {
            buildSignatureResubmissionPersonalisationDto(
                applicationReference = applicationReference,
                firstName = firstName,
                eroContactDetails = eroContactDetailsDto,
                sourceType = sourceTypeString,
                rejectionNotes = rejectionNotes,
                rejectionReasons = listOf(wrongSizeText, hasShadowsText),
                freeText = rejectionFreeText,
                deadline = deadlineString,
                uploadSignatureLink = uploadSignatureLink,
                signatureNotSuitableText = null,
            )
        }

        // When
        val actual = mapper.fromRequestToPersonalisationDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = ["false,false,0", "true,false,1", "true,true,0"],
    )
    fun `should include signature not suitable text only if rejected without rejection reasons`(
        isRejected: Boolean,
        includeReasons: Boolean,
        expectedMapperCalls: Int,
    ) {
        // Given
        val request = buildGenerateSignatureResubmissionTemplatePreviewRequest(
            sourceType = SourceType.POSTAL,
            language = Language.EN,
            personalisation = buildSignatureResubmissionPersonalisation(
                rejectionNotes = "Notes",
                rejectionReasons = if (includeReasons) listOf(SignatureRejectionReason.WRONG_MINUS_SIZE, SignatureRejectionReason.HAS_MINUS_SHADOWS, SignatureRejectionReason.OTHER) else emptyList(),
                rejectionFreeText = "Free Text",
                deadlineDate = LocalDate.of(2024, 10, 1),
                isRejected = isRejected,
            ),
        )
        val sourceTypeString = "Source Type"
        val eroContactDetailsDto = buildContactDetailsDto()
        val deadlineString = "Deadline"

        given(sourceTypeMapper.toSourceTypeString(request.sourceType, LanguageDto.ENGLISH)).willReturn(sourceTypeString)
        given(languageMapper.fromApiToDto(request.language!!)).willReturn(LanguageDto.ENGLISH)
        with(request.personalisation) {
            given(eroContactDetailsMapper.fromApiToDto(eroContactDetails)).willReturn(eroContactDetailsDto)
            given(sourceTypeMapper.toSourceTypeString(request.sourceType, LanguageDto.ENGLISH)).willReturn(sourceTypeString)
            given(sourceTypeMapper.toFullSourceTypeString(request.sourceType, LanguageDto.ENGLISH)).willReturn("Full String")
            given(deadlineMapper.toDeadlineString(deadlineDate!!, deadlineTime, LanguageDto.ENGLISH, "Full String")).willReturn(deadlineString)
        }
        // When
        mapper.fromRequestToPersonalisationDto(request)

        // Then
        verify(signatureRejectionReasonMapper, times(expectedMapperCalls)).toSignatureNotSuitableText(
            sourceTypeString,
            LanguageDto.ENGLISH,
        )
    }
}
