package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason.WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildPhotoResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel as CommunicationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel as CommunicationChannelApi
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class PhotoResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: PhotoResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var photoRejectionReasonMapper: PhotoRejectionReasonMapper

    @Test
    fun `should map photo template request to dto given no rejection reasons`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = CommunicationChannelApi.EMAIL,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = emptyList(),
                photoRejectionNotes = null,
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(CommunicationChannelDto.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = CommunicationChannelDto.EMAIL,
            language = LanguageDto.ENGLISH,
            notificationType = NotificationType.PHOTO_RESUBMISSION,
            personalisation = with(request.personalisation) {
                buildPhotoPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    photoRejectionReasons = emptyList(),
                    photoRejectionNotes = null,
                    photoRequestFreeText = photoRequestFreeText,
                    uploadPhotoLink = uploadPhotoLink,
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
                )
            },
        )

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(channelMapper).fromApiToDto(CommunicationChannelApi.EMAIL)
        verifyNoInteractions(photoRejectionReasonMapper)
    }

    @Test
    fun `should map photo template request to dto given rejection reasons`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = CommunicationChannelApi.EMAIL,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = listOf(
                    NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
                    WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
                    OTHER, // OTHER is deliberately excluded from the photo rejection reason mapping
                ),
                photoRejectionNotes = "Please take a head and shoulders photo, with a plain expression, and without sunglasses. Regular prescription glasses are acceptable.",
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(CommunicationChannelDto.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)

        given(photoRejectionReasonMapper.toPhotoRejectionReasonString(any<PhotoRejectionReason>(), any())).willReturn(
            "Not a plain facial expression",
            "Wearing sunglasses, or tinted glasses",
            // a mapping from OTHER is not expected - this is by design
        )

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = CommunicationChannelDto.EMAIL,
            language = LanguageDto.ENGLISH,
            notificationType = NotificationType.PHOTO_RESUBMISSION_WITH_REASONS,
            personalisation = with(request.personalisation) {
                buildPhotoPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    photoRejectionReasons = listOf("Not a plain facial expression", "Wearing sunglasses, or tinted glasses"),
                    photoRejectionNotes = "Please take a head and shoulders photo, with a plain expression, and without sunglasses. Regular prescription glasses are acceptable.",
                    photoRequestFreeText = photoRequestFreeText,
                    uploadPhotoLink = uploadPhotoLink,
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
                )
            },
        )

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(channelMapper).fromApiToDto(CommunicationChannelApi.EMAIL)
        verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
            NOT_MINUS_A_MINUS_PLAIN_MINUS_FACIAL_MINUS_EXPRESSION,
            LanguageDto.ENGLISH,
        )
        verify(photoRejectionReasonMapper).toPhotoRejectionReasonString(
            WEARING_MINUS_SUNGLASSES_MINUS_OR_MINUS_TINTED_MINUS_GLASSES,
            LanguageDto.ENGLISH,
        )
    }
}
