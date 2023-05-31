package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto.ENGLISH
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.OTHER
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.ADOPTION_MINUS_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.BIRTH_MINUS_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.FIREARMS_MINUS_CERTIFICATE
import uk.gov.dluhc.notificationsapi.models.DocumentType.MORTGAGE_MINUS_STATEMENT
import uk.gov.dluhc.notificationsapi.models.DocumentType.UTILITY_MINUS_BILL
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
            buildRejectedDocument(UTILITY_MINUS_BILL, listOf(DOCUMENT_MINUS_TOO_MINUS_OLD, UNREADABLE_MINUS_DOCUMENT), null),
            buildRejectedDocument(BIRTH_MINUS_CERTIFICATE, listOf(UNREADABLE_MINUS_DOCUMENT), null),
            buildRejectedDocument(MORTGAGE_MINUS_STATEMENT, listOf(OTHER), "Some notes"),
            buildRejectedDocument(FIREARMS_MINUS_CERTIFICATE, emptyList(), "More notes"),
            buildRejectedDocument(ADOPTION_MINUS_CERTIFICATE, emptyList(), null),
        )
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(DOCUMENT_MINUS_TOO_MINUS_OLD, ENGLISH)).willReturn("Document is too old")
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(UNREADABLE_MINUS_DOCUMENT, ENGLISH)).willReturn("Document is not readable")
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(OTHER, ENGLISH)).willReturn("other")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(UTILITY_MINUS_BILL, ENGLISH)).willReturn("Utility Bill")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(BIRTH_MINUS_CERTIFICATE, ENGLISH)).willReturn("Birth Certificate")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(MORTGAGE_MINUS_STATEMENT, ENGLISH)).willReturn("Mortgage statement")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(FIREARMS_MINUS_CERTIFICATE, ENGLISH)).willReturn("Firearms cert")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(ADOPTION_MINUS_CERTIFICATE, ENGLISH)).willReturn("Adoption cert")

        // When
        val actual = mapper.mapRejectionDocumentsFromApi(ENGLISH, documents)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(
            listOf(
                "Utility Bill - Document is too old, Document is not readable",
                "Birth Certificate - Document is not readable",
                "Mortgage statement - other - Some notes",
                "Firearms cert - More notes",
                "Adoption cert"
            )
        )
    }

    @Test
    fun `should map from Messaging Rejected documents to Strings`() {
        // Given
        val documents = listOf(
            buildRejectedDocumentMessaging(
                DocumentType.UTILITY_MINUS_BILL,
                listOf(DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD, DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT),
                null
            ),
            buildRejectedDocumentMessaging(DocumentType.BIRTH_MINUS_CERTIFICATE, listOf(DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT), null),
            buildRejectedDocumentMessaging(DocumentType.MORTGAGE_MINUS_STATEMENT, listOf(DocumentRejectionReason.OTHER), "Some notes"),
            buildRejectedDocumentMessaging(DocumentType.FIREARMS_MINUS_CERTIFICATE, emptyList(), "More notes"),
            buildRejectedDocumentMessaging(DocumentType.ADOPTION_MINUS_CERTIFICATE, emptyList(), null),
        )
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(DocumentRejectionReason.DOCUMENT_MINUS_TOO_MINUS_OLD, ENGLISH)).willReturn("Document is too old")
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(DocumentRejectionReason.UNREADABLE_MINUS_DOCUMENT, ENGLISH)).willReturn("Document is not readable")
        given(rejectedDocumentReasonMapper.toDocumentRejectionReasonString(DocumentRejectionReason.OTHER, ENGLISH)).willReturn("other")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(DocumentType.UTILITY_MINUS_BILL, ENGLISH)).willReturn("Utility Bill")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(DocumentType.BIRTH_MINUS_CERTIFICATE, ENGLISH)).willReturn("Birth Certificate")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(DocumentType.MORTGAGE_MINUS_STATEMENT, ENGLISH)).willReturn("Mortgage statement")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(DocumentType.FIREARMS_MINUS_CERTIFICATE, ENGLISH)).willReturn("Firearms cert")
        given(rejectedDocumentTypeMapper.toDocumentTypeString(DocumentType.ADOPTION_MINUS_CERTIFICATE, ENGLISH)).willReturn("Adoption cert")

        // When
        val actual = mapper.mapRejectionDocumentsFromMessaging(ENGLISH, documents)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(
            listOf(
                "Utility Bill - Document is too old, Document is not readable",
                "Birth Certificate - Document is not readable",
                "Mortgage statement - other - Some notes",
                "Firearms cert - More notes",
                "Adoption cert"
            )
        )
    }
}
