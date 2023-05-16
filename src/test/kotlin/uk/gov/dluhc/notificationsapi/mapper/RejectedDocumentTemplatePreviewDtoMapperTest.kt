package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.dto.RejectedDocumentTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.BIRTH_MINUS_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.MORTGAGE_MINUS_STATEMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.UTILITY_MINUS_BILL
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildAddressRequestWithOptionalParamsNull
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildEroContactDetails
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildGenerateRejectedDocumentTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class RejectedDocumentTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: RejectedDocumentTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var rejectedDocumentMapper: RejectedDocumentMapper

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should map rejected document template request to dto`(language: Language) {
        // Given
        val documentOne = buildRejectedDocument(documentType = UTILITY_MINUS_BILL, rejectionReason = DOCUMENT_MINUS_TOO_MINUS_OLD, rejectionNotes = null)
        val documentTwo = buildRejectedDocument(documentType = BIRTH_MINUS_CERTIFICATE, rejectionReason = UNREADABLE_MINUS_DOCUMENT, rejectionNotes = null)
        val documentThree = buildRejectedDocument(documentType = MORTGAGE_MINUS_STATEMENT, rejectionReason = OTHER, rejectionNotes = "Some notes")
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = SourceTypeModel.POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(documents = listOf(documentOne, documentTwo, documentThree))
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(SourceTypeModel.POSTAL)).willReturn(SourceTypeDto.POSTAL)
        given(rejectedDocumentMapper.fromApiRejectedDocumentToString(ENGLISH, documentOne)).willReturn("Utility Bill - Document is too old")
        given(rejectedDocumentMapper.fromApiRejectedDocumentToString(ENGLISH, documentTwo)).willReturn("Birth Certificate - Document is not readable")
        given(rejectedDocumentMapper.fromApiRejectedDocumentToString(ENGLISH, documentThree)).willReturn("Mortgage statement - other - Some notes")

        // When
        val actual = mapper.toRejectedDocumentTemplatePreviewDto(request)

        // Then
        val expected = RejectedDocumentTemplatePreviewDto(
            channel = NotificationChannel.EMAIL,
            sourceType = SourceTypeDto.POSTAL,
            language = ENGLISH,
            personalisation = with(request.personalisation) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf(
                        "Utility Bill - Document is too old",
                        "Birth Certificate - Document is not readable",
                        "Mortgage statement - other - Some notes"
                    ),
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
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }
        )
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should map rejected document template request to dto when optional fields null`(language: Language) {
        // Given
        val document = buildRejectedDocument(documentType = UTILITY_MINUS_BILL, rejectionReason = null, rejectionNotes = null)
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = SourceTypeModel.POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(document),
                rejectedDocumentFreeText = null,
                eroContactDetails = buildEroContactDetails(address = buildAddressRequestWithOptionalParamsNull())
            )
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(SourceTypeModel.POSTAL)).willReturn(SourceTypeDto.POSTAL)
        given(rejectedDocumentMapper.fromApiRejectedDocumentToString(ENGLISH, document)).willReturn("Utility Bill")

        // When
        val actual = mapper.toRejectedDocumentTemplatePreviewDto(request)

        // Then
        val expected = RejectedDocumentTemplatePreviewDto(
            channel = NotificationChannel.EMAIL,
            sourceType = SourceTypeDto.POSTAL,
            language = ENGLISH,
            personalisation = with(request.personalisation) {
                RejectedDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    documents = listOf("Utility Bill"),
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
                                    postcode = postcode
                                )
                            }
                        )
                    }
                )
            }
        )
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
