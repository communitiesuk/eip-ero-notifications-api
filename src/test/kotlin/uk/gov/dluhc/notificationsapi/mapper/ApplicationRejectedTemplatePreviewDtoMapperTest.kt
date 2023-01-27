package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.ApplicationRejectedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.INCOMPLETE_MINUS_APPLICATION
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
import uk.gov.dluhc.notificationsapi.models.ApplicationRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateApplicationRejectedTemplatePreviewRequest

@ExtendWith(MockitoExtension::class)
class ApplicationRejectedTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: ApplicationRejectedTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var applicationRejectionReasonMapper: ApplicationRejectionReasonMapper

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should map application rejected template request to dto`(language: Language) {
        // Given
        val request = buildGenerateApplicationRejectedTemplatePreviewRequest(language = language)
        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        val incompleteApplication = "Application is incomplete"
        val applicantHasNotResponded = "Applicant has not responded to requests for information"
        val other = "other"
        given(applicationRejectionReasonMapper.toApplicationRejectionReasonMessage(INCOMPLETE_MINUS_APPLICATION))
            .willReturn(incompleteApplication)
        given(
            applicationRejectionReasonMapper.toApplicationRejectionReasonMessage(
                NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
            )
        ).willReturn(applicantHasNotResponded)
        given(applicationRejectionReasonMapper.toApplicationRejectionReasonMessage(OTHER)).willReturn(other)

        val expected = ApplicationRejectedTemplatePreviewDto(
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                ApplicationRejectedPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    rejectionReasonList = listOf(incompleteApplication, applicantHasNotResponded, other),
                    rejectionReasonMessage = rejectionReasonMessage,
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
                    }
                )
            }
        )

        // When
        val actual = mapper.toApplicationRejectedTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(language)
        verify(applicationRejectionReasonMapper).toApplicationRejectionReasonMessage(INCOMPLETE_MINUS_APPLICATION)
        verify(applicationRejectionReasonMapper).toApplicationRejectionReasonMessage(
            NO_MINUS_RESPONSE_MINUS_FROM_MINUS_APPLICANT
        )
        verify(applicationRejectionReasonMapper).toApplicationRejectionReasonMessage(OTHER)
        verifyNoMoreInteractions(applicationRejectionReasonMapper)
    }
}
