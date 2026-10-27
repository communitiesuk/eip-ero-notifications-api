package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_TOO_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.UNREADABLE_DOCUMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.ADOPTION_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.BIRTH_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.FIREARMS_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.GUARDIANSHIP_PROOF
import uk.gov.dluhc.notificationsapi.models.DocumentType.MORTGAGE_STATEMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.UTILITY_BILL
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument as buildRejectedDocumentMessaging

@ExtendWith(MockitoExtension::class)
class RejectedDocumentsMapperTest {

    @Mock
    private lateinit var rejectedDocumentReasonMapper: RejectedDocumentReasonMapper

    @Mock
    private lateinit var rejectedDocumentTypeMapper: RejectedDocumentTypeMapper

    private lateinit var mapper: RejectedDocumentsMapper

    @BeforeEach
    fun setUp() {
        mapper = RejectedDocumentsMapper(rejectedDocumentReasonMapper, rejectedDocumentTypeMapper)
    }

    @Test
    fun `should map from Api Rejected documents to Strings`() {
        // Given
        val documents = listOf(
            buildRejectedDocument(
                UTILITY_BILL,
                listOf(DOCUMENT_TOO_OLD, UNREADABLE_DOCUMENT, OTHER),
                null,
            ),
            buildRejectedDocument(BIRTH_CERTIFICATE, listOf(UNREADABLE_DOCUMENT), null),
            buildRejectedDocument(MORTGAGE_STATEMENT, listOf(DOCUMENT_TOO_OLD, OTHER), "Some notes"),
            buildRejectedDocument(FIREARMS_CERTIFICATE, emptyList(), "More notes"),
            buildRejectedDocument(ADOPTION_CERTIFICATE, emptyList(), null),
            buildRejectedDocument(GUARDIANSHIP_PROOF, emptyList(), "Guardianship notes"),
        )
        given(
            rejectedDocumentReasonMapper.toDocumentRejectionReasonString(
                DOCUMENT_TOO_OLD,
                ENGLISH,
            ),
        ).willReturn("Document is too old")
        given(
            rejectedDocumentReasonMapper.toDocumentRejectionReasonString(
                UNREADABLE_DOCUMENT,
                ENGLISH,
            ),
        ).willReturn("Document is not readable")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(UTILITY_BILL, ENGLISH)).willReturn("Utility Bill")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                BIRTH_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Birth Certificate")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                MORTGAGE_STATEMENT,
                ENGLISH,
            ),
        ).willReturn("Mortgage statement")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                FIREARMS_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Firearms cert")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                ADOPTION_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Adoption cert")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                GUARDIANSHIP_PROOF,
                ENGLISH,
            ),
        ).willReturn("Document proving your connection with your guardian")

        // When
        val actual = mapper.mapRejectionDocumentsFromApi(ENGLISH, documents)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(
            listOf(
                "Utility Bill\n" +
                    "  * Document is too old\n" +
                    "  * Document is not readable",
                "Birth Certificate\n" +
                    "  * Document is not readable",
                "Mortgage statement\n" +
                    "  * Document is too old\n" +
                    "  * Some notes",
                "Firearms cert\n" +
                    "  * More notes",
                "Adoption cert",
                "Document proving your connection with your guardian\n" +
                    "  * Guardianship notes",
            ),
        )

        verify(rejectedDocumentReasonMapper, times(2)).toDocumentRejectionReasonString(
            DOCUMENT_TOO_OLD,
            ENGLISH,
        )
        verify(rejectedDocumentReasonMapper, times(2)).toDocumentRejectionReasonString(
            UNREADABLE_DOCUMENT,
            ENGLISH,
        )
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(UTILITY_BILL, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(BIRTH_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(MORTGAGE_STATEMENT, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(FIREARMS_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(ADOPTION_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(GUARDIANSHIP_PROOF, ENGLISH)
        verifyNoMoreInteractions(rejectedDocumentReasonMapper, rejectedDocumentTypeMapper)
    }

    @Test
    fun `should map from Messaging Rejected documents to Strings`() {
        // Given
        val documents = listOf(
            buildRejectedDocumentMessaging(
                DocumentType.UTILITY_BILL,
                listOf(
                    DocumentRejectionReason.DOCUMENT_TOO_OLD,
                    DocumentRejectionReason.UNREADABLE_DOCUMENT,
                    DocumentRejectionReason.OTHER,
                ),
                null,
            ),
            buildRejectedDocumentMessaging(
                DocumentType.BIRTH_CERTIFICATE,
                listOf(DocumentRejectionReason.UNREADABLE_DOCUMENT),
                null,
            ),
            buildRejectedDocumentMessaging(
                DocumentType.MORTGAGE_STATEMENT,
                listOf(DocumentRejectionReason.DOCUMENT_TOO_OLD, DocumentRejectionReason.OTHER),
                "Some notes",
            ),
            buildRejectedDocumentMessaging(DocumentType.FIREARMS_CERTIFICATE, emptyList(), "More notes"),
            buildRejectedDocumentMessaging(DocumentType.ADOPTION_CERTIFICATE, emptyList(), null),
            buildRejectedDocumentMessaging(DocumentType.GUARDIANSHIP_PROOF, emptyList(), null),
            buildRejectedDocumentMessaging(DocumentType.INSURANCE_PROVIDER_LETTER, emptyList(), "Notes"),
        )
        given(
            rejectedDocumentReasonMapper.toDocumentRejectionReasonString(
                DocumentRejectionReason.DOCUMENT_TOO_OLD,
                ENGLISH,
            ),
        ).willReturn("Document is too old")
        given(
            rejectedDocumentReasonMapper.toDocumentRejectionReasonString(
                DocumentRejectionReason.UNREADABLE_DOCUMENT,
                ENGLISH,
            ),
        ).willReturn("Document is not readable")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.UTILITY_BILL,
                ENGLISH,
            ),
        ).willReturn("Utility Bill")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.BIRTH_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Birth Certificate")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.MORTGAGE_STATEMENT,
                ENGLISH,
            ),
        ).willReturn("Mortgage statement")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.FIREARMS_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Firearms cert")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.ADOPTION_CERTIFICATE,
                ENGLISH,
            ),
        ).willReturn("Adoption cert")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.GUARDIANSHIP_PROOF,
                ENGLISH,
            ),
        ).willReturn("Document proving your connection with your guardian")
        given(
            rejectedDocumentTypeMapper.toDocumentTypeString(
                DocumentType.INSURANCE_PROVIDER_LETTER,
                ENGLISH,
            ),
        ).willReturn("Letter from an insurance provider")

        // When
        val actual = mapper.mapRejectionDocumentsFromMessaging(ENGLISH, documents)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(
            listOf(
                "Utility Bill\n" +
                    "  * Document is too old\n" +
                    "  * Document is not readable",
                "Birth Certificate\n" +
                    "  * Document is not readable",
                "Mortgage statement\n" +
                    "  * Document is too old\n" +
                    "  * Some notes",
                "Firearms cert\n" +
                    "  * More notes",
                "Adoption cert",
                "Document proving your connection with your guardian",
                "Letter from an insurance provider\n" +
                    "  * Notes",
            ),
        )

        verify(
            rejectedDocumentReasonMapper,
            times(2),
        ).toDocumentRejectionReasonString(DocumentRejectionReason.DOCUMENT_TOO_OLD, ENGLISH)
        verify(
            rejectedDocumentReasonMapper,
            times(2),
        ).toDocumentRejectionReasonString(DocumentRejectionReason.UNREADABLE_DOCUMENT, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.UTILITY_BILL, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.BIRTH_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.MORTGAGE_STATEMENT, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.FIREARMS_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.ADOPTION_CERTIFICATE, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(DocumentType.GUARDIANSHIP_PROOF, ENGLISH)
        verify(rejectedDocumentTypeMapper).toDocumentTypeString(
            DocumentType.INSURANCE_PROVIDER_LETTER,
            ENGLISH,
        )
        verifyNoMoreInteractions(rejectedDocumentReasonMapper, rejectedDocumentTypeMapper)
    }
}
