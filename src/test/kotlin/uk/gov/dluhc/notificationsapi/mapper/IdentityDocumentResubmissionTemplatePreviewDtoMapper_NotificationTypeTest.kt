package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.dto.NotificationType
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION
import uk.gov.dluhc.notificationsapi.dto.NotificationType.ID_DOCUMENT_RESUBMISSION_WITH_REASONS
import uk.gov.dluhc.notificationsapi.models.CommunicationChannel
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DUPLICATE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.INVALID_MINUS_DOCUMENT_MINUS_COUNTRY
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.INVALID_MINUS_DOCUMENT_MINUS_TYPE
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.models.Language
import uk.gov.dluhc.notificationsapi.models.SourceType
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildGenerateIdDocumentResubmissionTemplatePreviewRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.api.buildIdDocumentResubmissionPersonalisationRequest
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class IdentityDocumentResubmissionTemplatePreviewDtoMapper_NotificationTypeTest {

    @InjectMocks
    private lateinit var mapper: IdentityDocumentResubmissionTemplatePreviewDtoMapperImpl

    @Mock
    private lateinit var languageMapper: LanguageMapper

    @Mock
    private lateinit var channelMapper: CommunicationChannelMapper

    @Mock
    private lateinit var sourceTypeMapper: SourceTypeMapper

    @Mock
    private lateinit var documentRejectionTextMapper: IdentityDocumentResubmissionDocumentRejectionTextMapper

    companion object {
        @JvmStatic
        fun documentRejectionReasons_to_NotificationType(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(emptyList<DocumentRejectionReason>(), ID_DOCUMENT_RESUBMISSION),
                Arguments.of(listOf(OTHER), ID_DOCUMENT_RESUBMISSION),
                Arguments.of(listOf(OTHER), ID_DOCUMENT_RESUBMISSION),

                Arguments.of(
                    listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(UNREADABLE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(INVALID_MINUS_DOCUMENT_MINUS_TYPE),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(DUPLICATE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(INVALID_MINUS_DOCUMENT_MINUS_COUNTRY),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),

                Arguments.of(
                    listOf(OTHER, DOCUMENT_MINUS_TOO_MINUS_OLD),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER, UNREADABLE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER, INVALID_MINUS_DOCUMENT_MINUS_TYPE),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER, DUPLICATE_MINUS_DOCUMENT),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER, INVALID_MINUS_DOCUMENT_MINUS_COUNTRY),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
                Arguments.of(
                    listOf(OTHER, APPLICANT_MINUS_DETAILS_MINUS_NOT_MINUS_CLEAR),
                    ID_DOCUMENT_RESUBMISSION_WITH_REASONS,
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("documentRejectionReasons_to_NotificationType")
    fun `should map ID document template request to dto with correct NotificationType mapping given rejection reasons and no rejection notes`(
        documentRejectionReasons: List<DocumentRejectionReason>,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = CommunicationChannel.LETTER,
            language = Language.EN,
            sourceType = SourceType.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = documentRejectionReasons,
                        rejectionNotes = null,
                    ),
                ),
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.CommunicationChannel.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD)

        // When
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            ", ID_DOCUMENT_RESUBMISSION",
            "'', ID_DOCUMENT_RESUBMISSION",
            "'Some rejection reason notes', ID_DOCUMENT_RESUBMISSION_WITH_REASONS",
        ],
    )
    fun `should map ID document template request to dto with correct NotificationType mapping given document with no rejection reasons and rejection notes`(
        documentRejectionNotes: String?,
        expectedNotificationType: NotificationType,
    ) {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = CommunicationChannel.LETTER,
            language = Language.EN,
            sourceType = SourceType.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = documentRejectionNotes,
                    ),
                ),
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.CommunicationChannel.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD)

        // When
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(expectedNotificationType)
    }

    @Test
    fun `should map ID document template request to dto with correct NotificationType mapping given all documents have rejection reasons or rejection notes`() {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = CommunicationChannel.LETTER,
            language = Language.EN,
            sourceType = SourceType.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = "a reason for rejecting the document",
                    ),
                    buildRejectedDocument(
                        rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                        rejectionNotes = null,
                    ),
                ),
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.CommunicationChannel.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD)

        // When
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(ID_DOCUMENT_RESUBMISSION_WITH_REASONS)
    }

    @Test
    fun `should map ID document template request to dto with correct NotificationType mapping given not all documents have rejection reasons or rejection notes`() {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = CommunicationChannel.LETTER,
            language = Language.EN,
            sourceType = SourceType.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = "a reason for rejecting the document",
                    ),
                    buildRejectedDocument(
                        rejectionReasons = listOf(DOCUMENT_MINUS_TOO_MINUS_OLD),
                        rejectionNotes = null,
                    ),
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = null,
                    ),
                ),
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.CommunicationChannel.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD)

        // When
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(ID_DOCUMENT_RESUBMISSION_WITH_REASONS)
    }

    @Test
    fun `should map ID document template request to dto with correct NotificationType mapping given no documents have rejection reasons or rejection notes`() {
        // Given
        val request = buildGenerateIdDocumentResubmissionTemplatePreviewRequest(
            channel = CommunicationChannel.LETTER,
            language = Language.EN,
            sourceType = SourceType.VOTER_MINUS_CARD,
            personalisation = buildIdDocumentResubmissionPersonalisationRequest(
                rejectedDocuments = listOf(
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = null,
                    ),
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = null,
                    ),
                    buildRejectedDocument(
                        rejectionReasons = emptyList(),
                        rejectionNotes = null,
                    ),
                ),
            ),
        )

        given(languageMapper.fromApiToDto(any())).willReturn(LanguageDto.ENGLISH)
        given(channelMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.CommunicationChannel.LETTER)
        given(sourceTypeMapper.fromApiToDto(any())).willReturn(uk.gov.dluhc.notificationsapi.dto.SourceType.VOTER_CARD)

        // When
        val actual = mapper.toIdDocumentResubmissionTemplatePreviewDto(request)

        // Then
        assertThat(actual.notificationType).isEqualTo(ID_DOCUMENT_RESUBMISSION)
    }
}
