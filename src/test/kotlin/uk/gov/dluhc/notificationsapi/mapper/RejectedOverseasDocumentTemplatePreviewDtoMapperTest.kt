package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.GenerateRejectedOverseasDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.OverseasDocumentTypeDto
import uk.gov.dluhc.notificationsapi.dto.RejectedOverseasDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.NotificationChannel
import uk.gov.dluhc.notificationsapi.models.OverseasDocumentType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedOverseasDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto

@ExtendWith(MockitoExtension::class)
class RejectedOverseasDocumentTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: RejectedOverseasDocumentTemplatePreviewDtoMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var notificationChannelMapper: NotificationChannelMapper

    @Mock
    private lateinit var overseasDocumentTypeMapper: OverseasDocumentTypeMapper

    @Mock
    private lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    @Mock
    private lateinit var eroDtoMapper: EroDtoMapper

    @ParameterizedTest
    @CsvSource(
        "PARENT_MINUS_GUARDIAN, PARENT_GUARDIAN",
        "IDENTITY, IDENTITY",
        "QUALIFYING_MINUS_ADDRESS, QUALIFYING_ADDRESS"
    )
    fun `should map rejected parent guardian template request to dto`(
        overseasDocumentType: OverseasDocumentType,
        overseasDocumentTypeDto: OverseasDocumentTypeDto
    ) {
        // Given
        val request = buildRejectedOverseasDocumentTemplatePreviewRequest(
            language = Language.EN,
            channel = NotificationChannel.EMAIL,
            overseasDocumentType = overseasDocumentType
        )
        val contactDetailsDto =
            with(request.personalisation.eroContactDetails) {
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


        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(notificationChannelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.NotificationChannel.EMAIL)
        given(overseasDocumentTypeMapper.fromApiToDto(any())).willReturn(overseasDocumentTypeDto)
        given(rejectedDocumentsMapper.mapRejectionDocumentsFromApi(any(), any())).willReturn(listOf("doc1", "doc2"))
        given(eroDtoMapper.toContactDetailsDto(any())).willReturn(contactDetailsDto)

        val expected = GenerateRejectedOverseasDocumentTemplatePreviewDto(
            language = LanguageDto.ENGLISH,
            overseasDocumentType = overseasDocumentTypeDto,
            personalisation = with(request.personalisation) {
                RejectedOverseasDocumentPersonalisationDto(
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
                    documents = listOf("doc1", "doc2"),
                    rejectedDocumentFreeText = null,
                )
            },

            channel = NotificationChannelDto.EMAIL,
        )

        // When
        val actual = mapper.toRejectedOverseasDocumentTemplatePreviewDto(request)

        // Then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
