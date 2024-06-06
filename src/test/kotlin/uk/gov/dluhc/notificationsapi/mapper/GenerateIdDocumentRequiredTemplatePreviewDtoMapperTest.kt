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
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.GenerateIdDocumentRequiredTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.IdDocumentRequiredPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL
import uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateIdDocumentRequiredTemplatePreviewRequest

@ExtendWith(MockitoExtension::class)
class GenerateIdDocumentRequiredTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: GenerateIdDocumentRequiredTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map id document required template request to dto`() {
        // Given
        val request = buildGenerateIdDocumentRequiredTemplatePreviewRequest(
            language = Language.EN,
            channel = NotificationChannel.EMAIL,
            sourceType = SourceType.VOTER_MINUS_CARD,
        )
        given(languageMapper.fromApiToDto(any())).willReturn(ENGLISH)
        given(notificationChannelMapper.fromApiToDto(any())).willReturn(EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(VOTER_CARD)

        val expected = GenerateIdDocumentRequiredTemplatePreviewDto(
            language = ENGLISH,
            personalisation = with(request.personalisation) {
                IdDocumentRequiredPersonalisationDto(
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
                    idDocumentRequiredFreeText = idDocumentRequiredFreeText,
                )
            },
            channel = EMAIL,
            sourceType = VOTER_CARD,
        )

        // When
        val actual = mapper.toGenerateIdDocumentRequiredTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(notificationChannelMapper).fromApiToDto(NotificationChannel.EMAIL)
        verify(sourceTypeMapper).fromApiToDto(SourceType.VOTER_MINUS_CARD)
        verifyNoMoreInteractions(languageMapper, notificationChannelMapper, sourceTypeMapper)
    }
}
