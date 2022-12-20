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
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoResubmissionPersonalisationDto

@ExtendWith(MockitoExtension::class)
class PhotoResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: PhotoResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Test
    fun `should map to dto`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = NotificationChannel.EMAIL,
            language = Language.EN
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            channel = uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildPhotoResubmissionPersonalisationDto(
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
    }
}
