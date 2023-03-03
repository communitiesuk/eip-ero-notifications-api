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
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateApplicationApprovedTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildApplicationApprovedPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateApplicationApprovedTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class GenerateApplicationApprovedTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: GenerateApplicationApprovedTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Test
    fun `should map application approved template request to dto`() {
        // Given
        val request = buildGenerateApplicationApprovedTemplatePreviewRequest(
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)

        val expected = buildGenerateApplicationApprovedTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                buildApplicationApprovedPersonalisationDto(
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
                            }
                        )
                    }
                )
            }
        )

        // When
        val actual = mapper.toApplicationApprovedTemplatePreviewDto(request)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
    }
}
