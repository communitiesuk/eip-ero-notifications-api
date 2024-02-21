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
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedParentGuardianTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.RejectedParentGuardianPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedParentGuardianTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto

@ExtendWith(MockitoExtension::class)
class RejectedParentGuardianTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: RejectedParentGuardianTemplatePreviewDtoMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Mock
    private lateinit var eroDtoMapper: EroDtoMapper

    @Test
    fun `should map rejected parent guardian template request to dto`() {
        // Given
        val request = buildRejectedParentGuardianTemplatePreviewRequest(
            language = Language.EN,
            channel = NotificationChannel.EMAIL,
        )
        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(notificationChannelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.OVERSEAS)
        given(rejectedDocumentsMapper.mapRejectionDocumentsFromApi(any(), any())).willReturn()

        val expected = GenerateRejectedParentGuardianTemplatePreviewDto(
            language = LanguageDto.ENGLISH,
            personalisation = with(request.personalisation) {
                RejectedParentGuardianPersonalisationDto(
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
                    documents = listOf("Doc1", "Doc2"),
                    rejectedDocumentFreeText = null,
                )
            },
            sourceType = SourceTypeDto.OVERSEAS,
            channel = NotificationChannelDto.EMAIL,
        )

        // When
        val actual = mapper.toRejectedParentGuardianTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(languageMapper).fromApiToDto(Language.EN)
        verify(notificationChannelMapper).fromApiToDto(NotificationChannel.EMAIL)
        verify(sourceTypeMapper).fromApiToDto(SourceType.OVERSEAS)
        verifyNoMoreInteractions(languageMapper, notificationChannelMapper, sourceTypeMapper)
    }
}
