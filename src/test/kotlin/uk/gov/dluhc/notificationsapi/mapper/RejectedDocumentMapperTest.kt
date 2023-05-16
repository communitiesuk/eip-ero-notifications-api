package uk.gov.dluhc.notificationsapi.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import uk.gov.dluhc.notificationsapi.dto.LanguageDto
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentRejectionReason as DocumentRejectionReasonMessaging
import uk.gov.dluhc.notificationsapi.messaging.models.DocumentType as DocumentTypeMessaging
import uk.gov.dluhc.notificationsapi.models.DocumentRejectionReason as DocumentRejectionReasonApi
import uk.gov.dluhc.notificationsapi.models.DocumentType as DocumentTypeApi
import uk.gov.dluhc.notificationsapi.testsupport.testdata.messaging.models.buildRejectedDocument as buildRejectedDocumentMessaging
import uk.gov.dluhc.notificationsapi.testsupport.testdata.models.buildRejectedDocument as buildRejectedDocumentApi

@ExtendWith(MockitoExtension::class)
class RejectedDocumentMapperTest {

    @Mock
    private lateinit var rejectedDocumentReasonMapper: RejectedDocumentReasonMapper

    @Mock
    private lateinit var rejectedDocumentTypeMapper: RejectedDocumentTypeMapper

    private lateinit var mapper: RejectedDocumentMapper

    @BeforeEach
    fun setUp() {
        mapper = RejectedDocumentMapper(rejectedDocumentReasonMapper, rejectedDocumentTypeMapper)
    }

    companion object {
        val LANGUAGE = LanguageDto.ENGLISH
        const val DOC_TYPE_STRING = "Util bill"
        const val DOC_REASON_STRING = "too old"
        const val NOTES_STRING = "some reason"
    }

    @Test
    fun `should map from rejected document api to string`() {
        given(rejectedDocumentTypeMapper.fromApiToString(DocumentTypeApi.UTILITY_MINUS_BILL, LANGUAGE)).willReturn(
            DOC_TYPE_STRING
        )
        given(
            rejectedDocumentReasonMapper.fromApiToString(
                DocumentRejectionReasonApi.DOCUMENT_MINUS_TOO_MINUS_OLD,
                LANGUAGE
            )
        ).willReturn(DOC_REASON_STRING)

        val result =
            mapper.fromApiRejectedDocumentToString(LANGUAGE, buildRejectedDocumentApi(rejectionNotes = NOTES_STRING))

        assertThat(result).isEqualTo("Util bill - too old - some reason")
    }

    @Test
    fun `should map from rejected document api to string when no optional values`() {
        given(rejectedDocumentTypeMapper.fromApiToString(DocumentTypeApi.UTILITY_MINUS_BILL, LANGUAGE)).willReturn(
            DOC_TYPE_STRING
        )

        val result = mapper.fromApiRejectedDocumentToString(
            LANGUAGE,
            buildRejectedDocumentApi(rejectionReason = null, rejectionNotes = null)
        )

        assertThat(result).isEqualTo("Util bill")
    }

    @Test
    fun `should map from rejected document message to string`() {
        given(
            rejectedDocumentTypeMapper.fromMessagingToString(
                DocumentTypeMessaging.UTILITY_MINUS_BILL,
                LANGUAGE
            )
        ).willReturn(DOC_TYPE_STRING)
        given(
            rejectedDocumentReasonMapper.fromMessagingToString(
                DocumentRejectionReasonMessaging.DUPLICATE_MINUS_DOCUMENT,
                LANGUAGE
            )
        ).willReturn(DOC_REASON_STRING)

        val result = mapper.fromMessagingRejectedDocumentToString(
            LANGUAGE,
            buildRejectedDocumentMessaging(rejectionNotes = NOTES_STRING)
        )

        assertThat(result).isEqualTo("Util bill - too old - some reason")
    }

    @Test
    fun `should map from rejected document message to string when no optional values`() {
        given(
            rejectedDocumentTypeMapper.fromMessagingToString(
                DocumentTypeMessaging.UTILITY_MINUS_BILL,
                LANGUAGE
            )
        ).willReturn(DOC_TYPE_STRING)

        val result = mapper.fromMessagingRejectedDocumentToString(
            LANGUAGE,
            buildRejectedDocumentMessaging(rejectionReason = null, rejectionNotes = null)
        )

        assertThat(result).isEqualTo("Util bill")
    }
}
