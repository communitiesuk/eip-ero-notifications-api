package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.GenerateParentGuardianRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.ParentGuardianPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildParentGuardianTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class ParentGuardianRequiredTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: ParentGuardianRequiredTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map id document required template request to dto`() {
        // Given
        val request = buildParentGuardianTemplatePreviewRequest(
            language = Language.EN,
            channel = NotificationChannel.EMAIL,
        )
        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(notificationChannelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.OVERSEAS)

        val expected = GenerateParentGuardianRequiredTemplatePreviewDto(
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                ParentGuardianPersonalisationDto(
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
                                    postcode = postcode
                                )
                            },

                            )
                    },
                    freeText = null,
                )
            },
            sourceType = SourceTypeDto.OVERSEAS,
            channel = NotificationChannelDto.EMAIL,
        )

        // When
        val actual = mapper.toParentGuardianRequiredTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(notificationChannelMapper).fromApiToDto(NotificationChannel.EMAIL)
        verify(sourceTypeMapper).fromApiToDto(SourceType.OVERSEAS)
        verifyNoMoreInteractions(languageMapper, notificationChannelMapper, sourceTypeMapper)
    }
}