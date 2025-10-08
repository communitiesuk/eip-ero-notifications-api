package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import java.time.LocalDate
import uk.gov.dluhc.notificationsapi.messaging.models.Language as LanguageMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SignatureRejectionReason as SignatureRejectionReasonMessage
import uk.gov.dluhc.notificationsapi.messaging.models.SourceType as SourceTypeMessage
import uk.gov.dluhc.notificationsapi.models.Language as LanguageApi
import uk.gov.dluhc.notificationsapi.models.SignatureRejectionReason as SignatureRejectionReasonApi
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApi
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildSignatureResubmissionPersonalisation as buildSignatureResubmissionPersonalisationMessage
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildSignatureResubmissionPersonalisation as buildSignatureResubmissionPersonalisationApi

@ExtendWith(MockitoExtension::class)
internal class SignatureResubmissionPersonalisationMapperTest {

    @InjectMocks
    private lateinit var personalisationMapper: SignatureResubmissionPersonalisationMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var deadlineMapper: DeadlineMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var eroContactDetailsMapper: EroContactDetailsMapper

    @Mock
    private lateinit var signatureRejectionReasonMapper: SignatureRejectionReasonMapper

    @Nested
    inner class FromMessagePersonalisationToPersonalisationMap {
        @Test
        fun `should map to personalisation map`() {
            // Given
            val personalisationMessage = buildSignatureResubmissionPersonalisationMessage()

            val dtoContactDetails = buildContactDetailsDto()

            given(languageMapper.fromMessageToDto(LanguageMessage.EN)).willReturn(ENGLISH)
            given(sourceTypeMapper.toShortSourceTypeString(SourceTypeMessage.POSTAL, ENGLISH)).willReturn("Short mapped source type")
            given(sourceTypeMapper.toFullSourceTypeString(SourceTypeMessage.POSTAL, ENGLISH)).willReturn("Full mapped source type")
            given(eroContactDetailsMapper.fromSqsToDto(personalisationMessage.eroContactDetails)).willReturn(dtoContactDetails)
            given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReasonMessage.TOO_MINUS_DARK, ENGLISH)).willReturn("Too Dark")
            given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReasonMessage.HAS_MINUS_SHADOWS, ENGLISH)).willReturn("Has Shadows")
            given(deadlineMapper.toDeadlineString(any(), any(), any(), any())).willReturn("Mapped deadline")

            val expectedPersonalisationMap = mapOf(
                "applicationReference" to personalisationMessage.applicationReference,
                "firstName" to personalisationMessage.firstName,
                "rejectionNotes" to personalisationMessage.rejectionNotes,
                "rejectionReasons" to listOf("Too Dark", "Has Shadows"),
                "rejectionFreeText" to personalisationMessage.rejectionFreeText,
                "LAName" to dtoContactDetails.localAuthorityName,
                "eroPhone" to dtoContactDetails.phone,
                "eroWebsite" to dtoContactDetails.website,
                "eroEmail" to dtoContactDetails.email,
                "eroAddressLine1" to dtoContactDetails.address.property,
                "eroAddressLine2" to dtoContactDetails.address.street,
                "eroAddressLine3" to dtoContactDetails.address.town,
                "eroAddressLine4" to dtoContactDetails.address.area,
                "eroAddressLine5" to dtoContactDetails.address.locality,
                "eroPostcode" to dtoContactDetails.address.postcode,
                "fullSourceType" to "Full mapped source type",
                "shortSourceType" to "Short mapped source type",
                "deadline" to "Mapped deadline",
                "uploadSignatureLink" to personalisationMessage.uploadSignatureLink,
                "signatureNotSuitableText" to "",
            )

            // When
            val actual =
                personalisationMapper.fromMessagePersonalisationToPersonalisationMap(personalisationMessage, SourceTypeMessage.POSTAL, LanguageMessage.EN)
            // Then
            assertThat(actual).isEqualTo(expectedPersonalisationMap)
        }

        @ParameterizedTest
        @CsvSource(
            value = ["false,false,false", "true,false,true", "true,true,false"],
        )
        fun `should include signature not suitable text only if rejected without rejection reasons`(
            isRejected: Boolean,
            includeReasons: Boolean,
            includeText: Boolean,
        ) {
            // Given
            val personalisationMessage = buildSignatureResubmissionPersonalisationMessage(
                rejectionNotes = "Notes",
                rejectionReasons = if (includeReasons) listOf(SignatureRejectionReasonMessage.WRONG_MINUS_SIZE, SignatureRejectionReasonMessage.HAS_MINUS_SHADOWS, SignatureRejectionReasonMessage.OTHER) else emptyList(),
                rejectionFreeText = "Free Text",
                deadlineDate = LocalDate.of(2024, 10, 1),
                isRejected = isRejected,
            )

            val eroContactDetailsDto = buildContactDetailsDto()
            val deadlineString = "Deadline"

            given(languageMapper.fromMessageToDto(LanguageMessage.EN)).willReturn(ENGLISH)
            given(eroContactDetailsMapper.fromSqsToDto(personalisationMessage.eroContactDetails)).willReturn(eroContactDetailsDto)
            given(sourceTypeMapper.toShortSourceTypeString(SourceTypeMessage.POSTAL, ENGLISH)).willReturn("Short String")
            given(sourceTypeMapper.toFullSourceTypeString(SourceTypeMessage.POSTAL, ENGLISH)).willReturn("Full String")
            given(deadlineMapper.toDeadlineString(personalisationMessage.deadlineDate!!, personalisationMessage.deadlineTime, ENGLISH, "Full String")).willReturn(deadlineString)

            // When
            personalisationMapper.fromMessagePersonalisationToPersonalisationMap(personalisationMessage, SourceTypeMessage.POSTAL, LanguageMessage.EN)

            // Then
            verify(signatureRejectionReasonMapper).toSignatureNotSuitableText(
                "Full String",
                ENGLISH,
                includeText,
            )
        }
    }

    @Nested
    inner class FromApiPersonalisationToPersonalisationMap {

        @Test
        fun `should map to personalisation map`() {
            // Given
            val personalisationRequest = buildSignatureResubmissionPersonalisationApi(
                rejectionNotes = "Notes",
                rejectionReasons = listOf(SignatureRejectionReasonApi.WRONG_MINUS_SIZE, SignatureRejectionReasonApi.HAS_MINUS_SHADOWS, SignatureRejectionReasonApi.OTHER),
                rejectionFreeText = "Free Text",
                deadlineDate = LocalDate.of(2024, 10, 1),
            )

            val eroContactDetailsDto = buildContactDetailsDto()
            val wrongSizeText = "Wrong Size"
            val hasShadowsText = "Has Shadows"
            val deadlineString = "Deadline"

            given(languageMapper.fromApiToDto(LanguageApi.EN)).willReturn(ENGLISH)
            given(sourceTypeMapper.toShortSourceTypeString(SourceTypeApi.POSTAL, ENGLISH)).willReturn("Short String")
            given(sourceTypeMapper.toFullSourceTypeString(SourceTypeApi.POSTAL, ENGLISH)).willReturn("Full String")
            given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReasonApi.WRONG_MINUS_SIZE, ENGLISH)).willReturn(wrongSizeText)
            given(signatureRejectionReasonMapper.toSignatureRejectionReasonString(SignatureRejectionReasonApi.HAS_MINUS_SHADOWS, ENGLISH)).willReturn(hasShadowsText)
            given(eroContactDetailsMapper.fromApiToDto(personalisationRequest.eroContactDetails)).willReturn(eroContactDetailsDto)
            given(deadlineMapper.toDeadlineString(personalisationRequest.deadlineDate!!, personalisationRequest.deadlineTime, ENGLISH, "Full String")).willReturn(deadlineString)

            val expectedPersonalisationMap = mapOf(
                "applicationReference" to personalisationRequest.applicationReference,
                "firstName" to personalisationRequest.firstName,
                "rejectionNotes" to personalisationRequest.rejectionNotes,
                "rejectionReasons" to listOf(wrongSizeText, hasShadowsText),
                "rejectionFreeText" to personalisationRequest.rejectionFreeText,
                "LAName" to eroContactDetailsDto.localAuthorityName,
                "eroPhone" to eroContactDetailsDto.phone,
                "eroWebsite" to eroContactDetailsDto.website,
                "eroEmail" to eroContactDetailsDto.email,
                "eroAddressLine1" to eroContactDetailsDto.address.property,
                "eroAddressLine2" to eroContactDetailsDto.address.street,
                "eroAddressLine3" to eroContactDetailsDto.address.town,
                "eroAddressLine4" to eroContactDetailsDto.address.area,
                "eroAddressLine5" to eroContactDetailsDto.address.locality,
                "eroPostcode" to eroContactDetailsDto.address.postcode,
                "fullSourceType" to "Full String",
                "shortSourceType" to "Short String",
                "deadline" to deadlineString,
                "uploadSignatureLink" to personalisationRequest.uploadSignatureLink,
                "signatureNotSuitableText" to "",
            )

            // When
            val actual = personalisationMapper.fromApiPersonalisationToPersonalisationMap(
                personalisationRequest,
                SourceTypeApi.POSTAL,
                LanguageApi.EN,
            )

            // Then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expectedPersonalisationMap)
        }

        @ParameterizedTest
        @CsvSource(
            value = ["false,false,false", "true,false,true", "true,true,false"],
        )
        fun `should include signature not suitable text only if rejected without rejection reasons`(
            isRejected: Boolean,
            includeReasons: Boolean,
            includeText: Boolean,
        ) {
            // Given
            val personalisationRequest = buildSignatureResubmissionPersonalisationApi(
                rejectionNotes = "Notes",
                rejectionReasons = if (includeReasons) listOf(SignatureRejectionReasonApi.WRONG_MINUS_SIZE, SignatureRejectionReasonApi.HAS_MINUS_SHADOWS, SignatureRejectionReasonApi.OTHER) else emptyList(),
                rejectionFreeText = "Free Text",
                deadlineDate = LocalDate.of(2024, 10, 1),
                isRejected = isRejected,
            )

            val eroContactDetailsDto = buildContactDetailsDto()
            val deadlineString = "Deadline"

            given(languageMapper.fromApiToDto(LanguageApi.EN)).willReturn(ENGLISH)
            given(eroContactDetailsMapper.fromApiToDto(personalisationRequest.eroContactDetails)).willReturn(eroContactDetailsDto)
            given(sourceTypeMapper.toShortSourceTypeString(SourceTypeApi.POSTAL, ENGLISH)).willReturn("Short String")
            given(sourceTypeMapper.toFullSourceTypeString(SourceTypeApi.POSTAL, ENGLISH)).willReturn("Full String")
            given(deadlineMapper.toDeadlineString(personalisationRequest.deadlineDate!!, personalisationRequest.deadlineTime, ENGLISH, "Full String")).willReturn(deadlineString)

            // When
            personalisationMapper.fromApiPersonalisationToPersonalisationMap(personalisationRequest, SourceTypeApi.POSTAL, LanguageApi.EN)

            // Then
            verify(signatureRejectionReasonMapper).toSignatureNotSuitableText(
                "Full String",
                ENGLISH,
                includeText,
            )
        }
    }
}
