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
import uk.gov.dluhc.notificationsapi.dto.SourceType
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGeneratePhotoResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGeneratePhotoResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildPhotoPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.models.NotificationChannel as NotificationChannelApi

@ExtendWith(MockitoExtension::class)
class ResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: ResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: NotificationChannelMapper

    @Test
    fun `should map photo template request to dto`() {
        // Given
        val request = buildGeneratePhotoResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.EMAIL,
            language = Language.EN
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.EMAIL)

        val expected = buildGeneratePhotoResubmissionTemplatePreviewDto(
            sourceType = SourceType.VOTER_CARD,
            channel = NotificationChannelDto.EMAIL,
            language = LanguageDto.ENGLISH,
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
    fun `should map ID document template request to dto`() {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.LETTER,
            language = Language.EN
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.LETTER)

        val expected = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
            sourceType = SourceType.VOTER_CARD,
            channel = NotificationChannelDto.LETTER,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildIdDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    idDocumentRequestFreeText = idDocumentRequestFreeText,
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
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(channelMapper).fromApiToDto(NotificationChannelApi.LETTER)
    }
}
