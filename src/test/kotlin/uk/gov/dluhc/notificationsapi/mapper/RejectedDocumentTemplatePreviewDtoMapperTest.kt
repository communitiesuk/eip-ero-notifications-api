package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.CommunicationChannel
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocumentPersonalisation
import java.util.stream.Stream
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class RejectedDocumentTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: RejectedDocumentTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var rejectedDocumentsMapper: RejectedDocumentsMapper

    companion object {
        @JvmStatic
        fun sourceType_to_language(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(SourceTypeModel.POSTAL, SourceTypeDto.POSTAL, Language.EN),
                Arguments.of(SourceTypeModel.POSTAL, SourceTypeDto.POSTAL, Language.CY),
                Arguments.of(SourceTypeModel.PROXY, SourceTypeDto.PROXY, Language.EN),
                Arguments.of(SourceTypeModel.PROXY, SourceTypeDto.PROXY, Language.CY),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("sourceType_to_language")
    fun `should map rejected document template request to dto`(
        sourceTypeModel: SourceTypeModel,
        sourceTypeDto: SourceTypeDto,
        language: Language,
    ) {
        // Given
        val documents = listOf(buildRejectedDocument())
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = sourceTypeModel,
            personalisation = buildRejectedDocumentPersonalisation(documents = documents),
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(sourceTypeModel)).willReturn(sourceTypeDto)
        given(sourceTypeMapper.toSourceTypeString(sourceTypeModel, ENGLISH)).willReturn("Mapped source type")
        given(rejectedDocumentsMapper.mapRejectionDocumentsFromApi(ENGLISH, documents)).willReturn(
            listOf(
                "Doc1",
                "Doc2",
            ),
        )

        // When
        val actual = mapper.toRejectedDocumentTemplatePreviewDto(request)

        // Then
        val expected = RejectedDocumentTemplatePreviewDto(
            channel = CommunicationChannel.EMAIL,
            sourceType = sourceTypeDto,
            language = ENGLISH,
            personalisation = with(request.personalisation) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf("Doc1", "Doc2"),
                    rejectedDocumentFreeText = rejectedDocumentFreeText,
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
                    personalisationSourceTypeString = "Mapped source type",
                )
            },
        )
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("sourceType_to_language")
    fun `should map rejected document template request to dto when optional fields null`(
        sourceTypeModel: SourceTypeModel,
        sourceTypeDto: SourceTypeDto,
        language: Language,
    ) {
        // Given
        val documents = listOf(buildRejectedDocument())
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = sourceTypeModel,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = documents,
                rejectedDocumentFreeText = null,
                eroContactDetails = buildEroContactDetails(address = buildAddressRequestWithOptionalParamsNull()),
            ),
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(sourceTypeModel)).willReturn(sourceTypeDto)
        given(sourceTypeMapper.toSourceTypeString(sourceTypeModel, ENGLISH)).willReturn("Mapped source type")
        given(rejectedDocumentsMapper.mapRejectionDocumentsFromApi(ENGLISH, documents)).willReturn(listOf("Doc1"))

        // When
        val actual = mapper.toRejectedDocumentTemplatePreviewDto(request)

        // Then
        val expected = RejectedDocumentTemplatePreviewDto(
            channel = CommunicationChannel.EMAIL,
            sourceType = sourceTypeDto,
            language = ENGLISH,
            personalisation = with(request.personalisation) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf("Doc1"),
                    rejectedDocumentFreeText = null,
                    eroContactDetails = with(eroContactDetails) {
                        buildContactDetailsDto(
                            localAuthorityName = localAuthorityName,
                            website = website,
                            phone = phone,
                            email = email,
                            address = with(address) {
                                buildAddressDto(
                                    street = street,
                                    property = null,
                                    locality = null,
                                    town = null,
                                    area = null,
                                    postcode = postcode,
                                )
                            },
                        )
                    },
                    personalisationSourceTypeString = "Mapped source type",
                )
            },
        )
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
