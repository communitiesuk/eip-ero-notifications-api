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
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var rejectedDocumentReasonMapper: RejectedDocumentReasonMapper

    @Mock
    private lateinit var rejectedDocumentTypeMapper: RejectedDocumentTypeMapper

    @ParameterizedTest
    @EnumSource(Language::class)
    fun `should map rejected document template request to dto`(language: Language) {
        // Given
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = SourceTypeModel.POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(
                    buildRejectedDocument(documentType = UTILITY_MINUS_BILL, rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD), rejectionNotes = null),
                    buildRejectedDocument(documentType = BIRTH_MINUS_CERTIFICATE, rejectionReasons = listOf(UNREADABLE_MINUS_DOCUMENT), rejectionNotes = null),
                    buildRejectedDocument(documentType = MORTGAGE_MINUS_STATEMENT, rejectionReasons = listOf(OTHER), rejectionNotes = "Some notes")
                )
            )
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(SourceTypeModel.POSTAL)).willReturn(SourceTypeDto.POSTAL)
        val tooOldReason = "Document is too old"
        val unreadableReason = "Document is not readable"
        val other = "other"
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(DOCUMENT_MINUS_TOO_MINUS_OLD, ENGLISH)).willReturn(tooOldReason)
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(UNREADABLE_MINUS_DOCUMENT, ENGLISH)).willReturn(unreadableReason)
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(OTHER, ENGLISH)).willReturn(other)
        val utilityBillDoc = "Utility Bill"
        val birthCertDoc = "Birth Certificate"
        val mortgageDoc = "Mortgage statement"
        given(rejectedDocumentTypeMapper.toDocumentTypeString(UTILITY_MINUS_BILL, ENGLISH)).willReturn(utilityBillDoc)
        given(rejectedDocumentTypeMapper.toDocumentTypeString(BIRTH_MINUS_CERTIFICATE, ENGLISH)).willReturn(birthCertDoc)
        given(rejectedDocumentTypeMapper.toDocumentTypeString(MORTGAGE_MINUS_STATEMENT, ENGLISH)).willReturn(mortgageDoc)

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
        val request = buildGenerateRejectedDocumentTemplatePreviewRequest(
            language = language,
            sourceType = SourceTypeModel.POSTAL,
            personalisation = buildRejectedDocumentPersonalisation(
                documents = listOf(buildRejectedDocument(documentType = UTILITY_MINUS_BILL, rejectionReasons = emptyList(), rejectionNotes = null)),
                rejectedDocumentFreeText = null,
                eroContactDetails = buildEroContactDetails(address = buildAddressRequestWithOptionalParamsNull())
            )
        )
        given(languageMapper.fromApiToDto(language)).willReturn(ENGLISH)
        given(sourceTypeMapper.fromApiToDto(SourceTypeModel.POSTAL)).willReturn(SourceTypeDto.POSTAL)
        val utilityBillDoc = "Utility Bill"
        given(rejectedDocumentTypeMapper.toDocumentTypeString(UTILITY_MINUS_BILL, ENGLISH)).willReturn(utilityBillDoc)

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
