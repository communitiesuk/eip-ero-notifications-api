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
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.PhotoRejectionReason
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildPhotoResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.NotificationChannel as NotificationChannelApi
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class PhotoResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: PhotoResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: NotificationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map photo template request to dto given no rejection reasons`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.EMAIL,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = emptyList()
            )
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = NotificationChannelDto.EMAIL,
            language = LanguageDto.ENGLISH,
            notificationType = NotificationType.PHOTO_RESUBMISSION,
            personalisation = with(request.personalisation) {
                buildPhotoPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
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
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }
        )

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(channelMapper).fromApiToDto(NotificationChannelApi.EMAIL)
    }

    @Test
    fun `should map photo template request to dto given rejection reasons`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.EMAIL,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = buildPhotoResubmissionPersonalisationRequest(
                photoRejectionReasons = listOf(PhotoRejectionReason.OTHER)
            )
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = NotificationChannelDto.EMAIL,
            language = LanguageDto.ENGLISH,
            notificationType = NotificationType.PHOTO_RESUBMISSION_WITH_REASONS,
            personalisation = with(request.personalisation) {
                buildPhotoPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
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
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }
        )

        // When
        val actual = mapper.toPhotoResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(channelMapper).fromApiToDto(NotificationChannelApi.EMAIL)
    }
}
