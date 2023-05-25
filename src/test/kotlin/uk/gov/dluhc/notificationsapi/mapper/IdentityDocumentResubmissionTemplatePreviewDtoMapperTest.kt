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
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentType
import uk.gov.dluhc.notificationsapi.models.IdDocumentPersonalisation
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildIdDocumentResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildAddressDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildContactDetailsDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildGenerateIdDocumentResubmissionTemplatePreviewDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.dto.buildIdDocumentPersonalisationDto
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.dto.NotificationChannel as NotificationChannelDto
import uk.gov.dluhc.notificationsapi.dto.SourceType as SourceTypeDto
import uk.gov.dluhc.notificationsapi.models.NotificationChannel as NotificationChannelApi
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeModel

@ExtendWith(MockitoExtension::class)
class IdentityDocumentResubmissionTemplatePreviewDtoMapperTest {

    @InjectMocks
    private lateinit var mapper: IdentityDocumentResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: NotificationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    @Test
    fun `should map ID document template request to dto given no rejected documents`() {
        // Given
        val personalisation = buildIdDocumentResubmissionPersonalisationRequest(
            rejectedDocuments = emptyList()
        )
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.LETTER,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = personalisation
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)
        given(documentRejectionTextMapper.toDocumentRejectionText(any(), any<IdDocumentPersonalisation>())).willReturn(
            null
        )

        val expected = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = NotificationChannelDto.LETTER,
            language = LanguageDto.ENGLISH,
            notificationType = ID_DOCUMENT_RESUBMISSION,
            personalisation = with(request.personalisation) {
                buildIdDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    idDocumentRequestFreeText = idDocumentRequestFreeText,
                    documentRejectionText = null,
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
        verify(sourceTypeMapper).fromApiToDto(SourceTypeModel.VOTER_MINUS_CARD)
        verify(documentRejectionTextMapper).toDocumentRejectionText(LanguageDto.ENGLISH, personalisation)
    }

    @Test
    fun `should map ID document template request to dto given rejected documents with rejection reasons`() {
        // Given
        val personalisation = buildIdDocumentResubmissionPersonalisationRequest(
            rejectedDocuments = listOf(
                buildRejectedDocument(
                    documentType = DocumentType.UTILITY_MINUS_BILL,
                    rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD)
                )
            )
        )
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = NotificationChannelApi.LETTER,
            language = Language.EN,
            sourceType = SourceTypeModel.VOTER_MINUS_CARD,
            personalisation = personalisation
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(NotificationChannelDto.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(SourceTypeDto.VOTER_CARD)
        val documentRejectionText = """
            Utility Bill
            
            * The document is too old
            
            ----
        
        """.trimIndent()
        given(documentRejectionTextMapper.toDocumentRejectionText(any(), any<IdDocumentPersonalisation>()))
            .willReturn(documentRejectionText)

        val expected = buildGenerateIdDocumentResubmissionTemplatePreviewDto(
            sourceType = SourceTypeDto.VOTER_CARD,
            channel = NotificationChannelDto.LETTER,
            language = LanguageDto.ENGLISH,
            notificationType = ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
            personalisation = with(request.personalisation) {
                buildIdDocumentPersonalisationDto(
                    applicationReference = applicationReference,
                    firstName = firstName,
                    idDocumentRequestFreeText = idDocumentRequestFreeText,
                    documentRejectionText = documentRejectionText,
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
        verify(sourceTypeMapper).fromApiToDto(SourceTypeModel.VOTER_MINUS_CARD)
        verify(documentRejectionTextMapper).toDocumentRejectionText(LanguageDto.ENGLISH, personalisation)
    }
}
